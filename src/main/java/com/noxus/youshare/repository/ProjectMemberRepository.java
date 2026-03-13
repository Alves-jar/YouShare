package com.noxus.youshare.repository;

import com.noxus.youshare.entity.ProjectMember;
import com.noxus.youshare.entity.ProjectMemberId;
import com.noxus.youshare.entity.enums.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
    List<ProjectMember> findByProjectId(UUID projectId);
    List<ProjectMember> findByUserId(UUID userId);
    boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);
    Optional<ProjectMember> findByProjectIdAndUserId(UUID projectId, UUID userId);
    List<ProjectMember> findByProjectIdAndInviteStatus(UUID projectId, InviteStatus inviteStatus);
}