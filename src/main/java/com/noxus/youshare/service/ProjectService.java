package com.noxus.youshare.service;

import com.noxus.youshare.dto.project.ProjectRequestDTO;
import com.noxus.youshare.dto.project.ProjectResponseDTO;
import com.noxus.youshare.dto.user.UserResponseDTO;
import com.noxus.youshare.entity.Project;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.entity.enums.ProjectStatus;
import com.noxus.youshare.exception.ProjectNotFoundException;
import com.noxus.youshare.exception.UnauthorizedException;
import com.noxus.youshare.exception.UserNotFoundException;
import com.noxus.youshare.repository.ProjectRepository;
import com.noxus.youshare.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final UserRepository userRepository;

    public ProjectResponseDTO create(ProjectRequestDTO newProject) {
        User creator = userRepository.findByUsername(newProject.creatorUsername())
            .orElseThrow(() -> new UserNotFoundException("User " + newProject.creatorUsername() + " not found"));

        Project project = new Project(
            null,
            newProject.title(),
            newProject.description(),
            creator,
            ProjectStatus.fromString(newProject.status()),
            null
        );

        Project saved = repository.save(project);
        return toDTO(saved);
    }

    public List<ProjectResponseDTO> findAll() {
        return repository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    public ProjectResponseDTO findById(UUID id) {
        Project project = repository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Project with id " + id + " not found"));

        return toDTO(project);
    }

    public ProjectResponseDTO update(UUID id, ProjectRequestDTO data, User authenticatedUser) {
        Project project = repository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Project with id " + id + " not found"));

        if (!project.getCreator().getId().equals(authenticatedUser.getId())) {
            throw new UnauthorizedException("Only the creator can edit this project");
        }

        project.setTitle(data.title());
        project.setDescription(data.description());
        project.setStatus(ProjectStatus.fromString(data.status()));

        Project saved = repository.save(project);
        return toDTO(saved);
    }

    public void delete(UUID id, User authenticatedUser) {
        Project project = repository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Project with id " + id + " not found"));

        if (!project.getCreator().getId().equals(authenticatedUser.getId())) {
            throw new UnauthorizedException("Only the creator can delete this project");
        }

        repository.delete(project);
    }

    private ProjectResponseDTO toDTO(Project project) {
        User creator = project.getCreator();
        UserResponseDTO creatorDTO = new UserResponseDTO(
            creator.getId(),
            creator.getUsername(),
            creator.getEmail(),
            creator.getRole(),
            creator.getCreatedAt()
        );

        return new ProjectResponseDTO(
            project.getId(),
            project.getTitle(),
            project.getDescription(),
            creatorDTO,
            project.getStatus(),
            project.getCreatedAt()
        );
    }
}