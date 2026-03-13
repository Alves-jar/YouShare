package com.noxus.youshare.service;

import com.noxus.youshare.dto.member.InviteRequestDTO;
import com.noxus.youshare.dto.member.ProjectMemberResponseDTO;
import com.noxus.youshare.entity.Project;
import com.noxus.youshare.entity.ProjectMember;
import com.noxus.youshare.entity.ProjectMemberId;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.entity.enums.InviteStatus;
import com.noxus.youshare.entity.enums.MemberRole;
import com.noxus.youshare.exception.ProjectNotFoundException;
import com.noxus.youshare.exception.UnauthorizedException;
import com.noxus.youshare.exception.UserNotFoundException;
import com.noxus.youshare.repository.ProjectMemberRepository;
import com.noxus.youshare.repository.ProjectRepository;
import com.noxus.youshare.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ProjectMemberResponseDTO invite(UUID projectId, InviteRequestDTO body, User authenticatedUser) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Project with id " + projectId + " not found"));

        if (!project.getCreator().getId().equals(authenticatedUser.getId())) {
            throw new UnauthorizedException("Only the project creator can invite members");
        }

        User invitedUser = userRepository.findByUsername(body.username())
            .orElseThrow(() -> new UserNotFoundException("User " + body.username() + " not found"));

        if (memberRepository.existsByProjectIdAndUserId(projectId, invitedUser.getId())) {
            throw new IllegalStateException("User " + body.username() + " is already invited or a member of this project");
        }

        ProjectMemberId memberId = new ProjectMemberId(invitedUser.getId(), projectId);
        ProjectMember member = new ProjectMember(
            memberId,
            invitedUser,
            project,
            MemberRole.EDITOR,
            InviteStatus.INVITED,
            null
        );

        ProjectMember saved = memberRepository.save(member);
        
        notificationService.createEditorInviteNotification(invitedUser, project, authenticatedUser);
        
        return toDTO(saved);
    }

    public ProjectMemberResponseDTO acceptInvite(UUID projectId, User authenticatedUser) {
        ProjectMember member = memberRepository
            .findByProjectIdAndUserId(projectId, authenticatedUser.getId())
            .orElseThrow(() -> new IllegalStateException("No pending invite found for this project"));

        if (member.getInviteStatus() == InviteStatus.ACCEPTED) {
            throw new IllegalStateException("Invite already accepted");
        }

        member.setInviteStatus(InviteStatus.ACCEPTED);
        ProjectMember saved = memberRepository.save(member);
        return toDTO(saved);
    }

    public List<ProjectMemberResponseDTO> findMembers(UUID projectId) {
        projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Project with id " + projectId + " not found"));

        return memberRepository.findByProjectId(projectId)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    private ProjectMemberResponseDTO toDTO(ProjectMember member) {
        return new ProjectMemberResponseDTO(
            member.getUser().getId(),
            member.getUser().getUsername(),
            member.getRole(),
            member.getInviteStatus(),
            member.getJoinedAt()
        );
    }
}