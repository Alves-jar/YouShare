package com.noxus.youshare.controller;

import com.noxus.youshare.dto.member.InviteRequestDTO;
import com.noxus.youshare.dto.member.ProjectMemberResponseDTO;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.service.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/members")
@AllArgsConstructor
@Tag(name = "Project Members", description = "Endpoints for project member management")
public class ProjectMemberController {

    private final ProjectMemberService service;

    @Operation(summary = "Invite member", description = "Sends an invitation for a user to join the project")
    @PostMapping("/invite")
    public ResponseEntity<ProjectMemberResponseDTO> invite(
            @Parameter(description = "Project ID", required = true) @PathVariable UUID projectId,
            @RequestBody InviteRequestDTO body,
            @AuthenticationPrincipal User authenticatedUser) {
        return ResponseEntity.ok(service.invite(projectId, body, authenticatedUser));
    }

    @Operation(summary = "Accept invitation", description = "Accepts an invitation to join a project")
    @PatchMapping("/accept")
    public ResponseEntity<ProjectMemberResponseDTO> accept(
            @Parameter(description = "Project ID", required = true) @PathVariable UUID projectId,
            @AuthenticationPrincipal User authenticatedUser) {
        return ResponseEntity.ok(service.acceptInvite(projectId, authenticatedUser));
    }

    @Operation(summary = "List members", description = "Returns all members of a project")
    @GetMapping
    public ResponseEntity<List<ProjectMemberResponseDTO>> findMembers(
            @Parameter(description = "Project ID", required = true) @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.findMembers(projectId));
    }
}