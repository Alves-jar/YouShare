package com.noxus.youshare.controller;

import com.noxus.youshare.dto.member.InviteRequestDTO;
import com.noxus.youshare.dto.member.ProjectMemberResponseDTO;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.service.ProjectMemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/members")
@AllArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService service;

    @PostMapping("/invite")
    public ResponseEntity<ProjectMemberResponseDTO> invite(
        @PathVariable UUID projectId,
        @RequestBody InviteRequestDTO body,
        @AuthenticationPrincipal User authenticatedUser
    ) {
        return ResponseEntity.ok(service.invite(projectId, body, authenticatedUser));
    }

    @PatchMapping("/accept")
    public ResponseEntity<ProjectMemberResponseDTO> accept(
        @PathVariable UUID projectId,
        @AuthenticationPrincipal User authenticatedUser
    ) {
        return ResponseEntity.ok(service.acceptInvite(projectId, authenticatedUser));
    }

    @GetMapping
    public ResponseEntity<List<ProjectMemberResponseDTO>> findMembers(
        @PathVariable UUID projectId
    ) {
        return ResponseEntity.ok(service.findMembers(projectId));
    }
}