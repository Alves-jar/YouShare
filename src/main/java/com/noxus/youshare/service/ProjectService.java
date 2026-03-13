package com.noxus.youshare.service;

import com.noxus.youshare.dto.project.ProjectRequestDTO;
import com.noxus.youshare.dto.project.ProjectResponseDTO;
import com.noxus.youshare.dto.user.UserResponseDTO;
import com.noxus.youshare.entity.Project;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.entity.enums.ProjectStatus;
import com.noxus.youshare.exception.UserNotFoundException;
import com.noxus.youshare.repository.ProjectRepository;
import com.noxus.youshare.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final UserRepository userRepository;

    public ProjectResponseDTO create(ProjectRequestDTO newProject) {

        User creator = userRepository.findByUsername(newProject.creatorUsername())
            .orElseThrow(() -> new UserNotFoundException("User " + newProject.creatorUsername() + " not found"));

        UserResponseDTO creatorDTO = new UserResponseDTO(
            creator.getId(),
            creator.getUsername(),
            creator.getEmail(),
            creator.getRole(),
            creator.getCreatedAt()
        );

        Project project = new Project(
            null,
            newProject.title(),
            newProject.description(),
            creator,
            ProjectStatus.fromString(newProject.status()),
            null
        );

        repository.save(project);

        return new ProjectResponseDTO(
            null,
            newProject.title(),
            newProject.description(),
            creatorDTO,
            ProjectStatus.fromString(newProject.status()),
            null
        );
    }
}
