package com.noxus.youshare.service;

import com.noxus.youshare.entity.Project;
import com.noxus.youshare.entity.ProjectMember;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.entity.Video;
import com.noxus.youshare.entity.VideoVersion;
import com.noxus.youshare.entity.enums.MemberRole;
import com.noxus.youshare.entity.enums.ProjectStatus;
import com.noxus.youshare.entity.enums.VersionStatus;
import com.noxus.youshare.exception.UnauthorizedException;
import com.noxus.youshare.exception.VideoNotFoundException;
import com.noxus.youshare.exception.VideoVersionNotFoundException;
import com.noxus.youshare.repository.ProjectMemberRepository;
import com.noxus.youshare.repository.ProjectRepository;
import com.noxus.youshare.repository.VideoRepository;
import com.noxus.youshare.repository.VideoVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class VideoVersionService {

    @Autowired
    private VideoVersionRepository videoVersionRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Value("${app.video.upload-dir:uploads/videos}")
    private String uploadDir;

    @Value("${app.video.max-size:104857600}")
    private long maxFileSize;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "video/mp4",
        "video/avi",
        "video/mov",
        "video/wmv",
        "video/flv",
        "video/webm",
        "video/mkv"
    );

    @Transactional
    public VideoVersion uploadNewVersion(MultipartFile file, UUID videoId, User uploadedBy, String description) {
        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new VideoNotFoundException("Video not found"));

        validateFile(file);
        validateEditorPermission(uploadedBy, video.getProject());

        try {
            Integer nextVersionNumber = getNextVersionNumber(videoId);
            String filename = generateUniqueFilename(file.getOriginalFilename(), nextVersionNumber);
            Path filePath = createVersionUploadDirectory(videoId).resolve(filename);
            
            Files.copy(file.getInputStream(), filePath);

            VideoVersion version = new VideoVersion();
            version.setId(null);
            version.setVideo(video);
            version.setVersionNumber(nextVersionNumber);
            version.setFilename(filename);
            version.setOriginalFilename(file.getOriginalFilename());
            version.setContentType(file.getContentType());
            version.setFileSize(file.getSize());
            version.setFilePath(filePath.toString());
            version.setDescription(description);
            version.setStatus(VersionStatus.PENDING);
            version.setUploadedBy(uploadedBy);
            version.setUploadedAt(new Date());
            version.setApprovedBy(null);
            version.setApprovedAt(null);
            version.setRejectionReason(null);

            return videoVersionRepository.save(version);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store video version file", e);
        }
    }

    public List<VideoVersion> getVideoVersions(UUID videoId) {
        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new VideoNotFoundException("Video not found"));
        
        return videoVersionRepository.findByVideoIdOrderByVersionNumberDesc(videoId);
    }

    public VideoVersion getVersion(UUID versionId) {
        return videoVersionRepository.findById(versionId)
            .orElseThrow(() -> new VideoVersionNotFoundException("Video version not found"));
    }

    public byte[] getVersionContent(UUID versionId) {
        VideoVersion version = getVersion(versionId);
        
        try {
            return Files.readAllBytes(Paths.get(version.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read video version file", e);
        }
    }

    @Transactional
    public VideoVersion approveVersion(UUID versionId, User approvedBy) {
        VideoVersion version = getVersion(versionId);
        
        validateCreatorPermission(approvedBy, version.getVideo().getProject());
        
        if (version.getStatus() != VersionStatus.PENDING) {
            throw new IllegalStateException("Only pending versions can be approved");
        }

        version.setStatus(VersionStatus.APPROVED);
        version.setApprovedBy(approvedBy);
        version.setApprovedAt(new Date());
        version.setRejectionReason(null);

        VideoVersion savedVersion = videoVersionRepository.save(version);
        
        updateProjectStatusOnApproval(version.getVideo().getProject());
        
        return savedVersion;
    }

    @Transactional
    public VideoVersion rejectVersion(UUID versionId, User rejectedBy, String reason) {
        VideoVersion version = getVersion(versionId);
        
        validateCreatorPermission(rejectedBy, version.getVideo().getProject());
        
        if (version.getStatus() != VersionStatus.PENDING) {
            throw new IllegalStateException("Only pending versions can be rejected");
        }

        version.setStatus(VersionStatus.REJECTED);
        version.setApprovedBy(rejectedBy);
        version.setApprovedAt(new Date());
        version.setRejectionReason(reason);

        return videoVersionRepository.save(version);
    }

    public List<VideoVersion> getVersionsByUser(UUID userId) {
        return videoVersionRepository.findByUploadedById(userId);
    }

    public List<VideoVersion> getPendingVersions() {
        return videoVersionRepository.findByStatus(VersionStatus.PENDING);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid video format. Allowed formats: " + ALLOWED_CONTENT_TYPES);
        }
    }

    private void validateEditorPermission(User user, Project project) {
        if (project.getCreator().getId().equals(user.getId())) {
            return;
        }

        ProjectMember membership = projectMemberRepository
            .findByProjectIdAndUserId(project.getId(), user.getId())
            .orElse(null);

        if (membership == null || membership.getRole() != MemberRole.EDITOR) {
            throw new UnauthorizedException("Only editors can upload new versions");
        }
    }

    private void validateCreatorPermission(User user, Project project) {
        if (!project.getCreator().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only project creators can approve/reject versions");
        }
    }

    private Integer getNextVersionNumber(UUID videoId) {
        Integer maxVersion = videoVersionRepository.findMaxVersionNumberByVideoId(videoId);
        return maxVersion != null ? maxVersion + 1 : 1;
    }

    private String generateUniqueFilename(String originalFilename, Integer versionNumber) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + "_v" + versionNumber + extension;
    }

    private Path createVersionUploadDirectory(UUID videoId) throws IOException {
        Path videoDir = Paths.get(uploadDir, "versions", videoId.toString());
        if (!Files.exists(videoDir)) {
            Files.createDirectories(videoDir);
        }
        return videoDir;
    }

    private void updateProjectStatusOnApproval(Project project) {
        if (project.getStatus() == ProjectStatus.IN_EDITING) {
            project.setStatus(ProjectStatus.REVIEW);
            projectRepository.save(project);
        }
    }
}
