package com.noxus.youshare.controller;

import com.noxus.youshare.dto.project.ProjectRequestDTO;
import com.noxus.youshare.dto.project.ProjectResponseDTO;
import com.noxus.youshare.entity.Project;
import com.noxus.youshare.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/project")
@AllArgsConstructor
public class ProjectController {

    private final ProjectService service;

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
}
