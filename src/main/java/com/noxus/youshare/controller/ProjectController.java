package com.noxus.youshare.controller;

import com.noxus.youshare.dto.project.ProjectRequestDTO;
import com.noxus.youshare.dto.project.ProjectResponseDTO;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@AllArgsConstructor
@Tag(name = "Projects", description = "Endpoints for video project management")
public class ProjectController {

    private final ProjectService service;

    @Operation(summary = "Create new project", description = "Creates a new video project in the system")
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> create(@RequestBody ProjectRequestDTO body) {
        ProjectResponseDTO project = service.create(body);

        URI uri = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(project.id())
            .toUri();

        return ResponseEntity.created(uri).body(project);
    }

    @Operation(summary = "List all projects", description = "Returns a list of all registered projects")
    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get project by ID", description = "Returns details of a specific project")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> findById(
            @Parameter(description = "Project ID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Update project", description = "Updates an existing project's data")
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> update(
            @Parameter(description = "Project ID", required = true) @PathVariable UUID id,
            @RequestBody ProjectRequestDTO body,
            @AuthenticationPrincipal User authenticatedUser) {
        return ResponseEntity.ok(service.update(id, body, authenticatedUser));
    }

    @Operation(summary = "Delete project", description = "Removes a project from the system")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Project ID", required = true) @PathVariable UUID id,
            @AuthenticationPrincipal User authenticatedUser) {
        service.delete(id, authenticatedUser);
        return ResponseEntity.noContent().build();
    }
}