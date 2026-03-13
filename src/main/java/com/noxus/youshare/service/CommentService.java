package com.noxus.youshare.service;

import com.noxus.youshare.dto.video.CommentRequestDTO;
import com.noxus.youshare.entity.Comment;
import com.noxus.youshare.entity.Project;
import com.noxus.youshare.entity.ProjectMember;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.entity.VideoVersion;
import com.noxus.youshare.exception.UnauthorizedException;
import com.noxus.youshare.exception.VideoVersionNotFoundException;
import com.noxus.youshare.repository.CommentRepository;
import com.noxus.youshare.repository.ProjectMemberRepository;
import com.noxus.youshare.repository.VideoVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private VideoVersionRepository videoVersionRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Comment createComment(UUID versionId, User user, CommentRequestDTO requestDTO) {
        VideoVersion version = videoVersionRepository.findById(versionId)
            .orElseThrow(() -> new VideoVersionNotFoundException("Video version not found"));

        Project project = version.getVideo().getProject();
        validateMemberPermission(user, project);

        String content = requestDTO.getContent();
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        Comment comment = new Comment();
        comment.setVersion(version);
        comment.setUser(user);
        comment.setContent(content.trim());

        Comment savedComment = commentRepository.save(comment);

        if (!version.getUploadedBy().getId().equals(user.getId())) {
            notificationService.createNewCommentNotification(version.getUploadedBy(), version, user);
        }

        return savedComment;
    }

    public List<Comment> getCommentsByVersion(UUID versionId) {
        VideoVersion version = videoVersionRepository.findById(versionId)
            .orElseThrow(() -> new VideoVersionNotFoundException("Video version not found"));

        return commentRepository.findByVersionIdOrderByCreatedAtDesc(versionId);
    }

    private void validateMemberPermission(User user, Project project) {
        if (project.getCreator().getId().equals(user.getId())) {
            return;
        }

        ProjectMember membership = projectMemberRepository
            .findByProjectIdAndUserId(project.getId(), user.getId())
            .orElse(null);

        if (membership == null) {
            throw new UnauthorizedException("Only project members can comment on versions");
        }
    }
}
