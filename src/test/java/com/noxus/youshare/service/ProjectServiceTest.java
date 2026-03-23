package com.noxus.youshare.service;

import com.noxus.youshare.dto.project.ProjectRequestDTO;
import com.noxus.youshare.dto.project.ProjectResponseDTO;
import com.noxus.youshare.entity.Project;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.entity.enums.ProjectStatus;
import com.noxus.youshare.entity.enums.UserRole;
import com.noxus.youshare.repository.ProjectRepository;
import com.noxus.youshare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private User creator;
    private UUID projectId;
    private UUID creatorId;

    @BeforeEach
    void setUp() {
        creatorId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        creator = new User();
        creator.setId(creatorId);
        creator.setUsername("creator");
        creator.setEmail("creator@example.com");
        creator.setRole(UserRole.CREATOR);
        creator.setCreatedAt(new Date());

        project = new Project();
        project.setId(projectId);
        project.setTitle("Test Project");
        project.setDescription("Test Description");
        project.setCreator(creator);
        project.setStatus(ProjectStatus.IN_EDITING);
        project.setCreatedAt(new Date());
    }

    @Test
    void findAll_ShouldReturnListOfProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<ProjectResponseDTO> result = projectService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).title());
    }

    @Test
    void findById_ShouldReturnProject() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ProjectResponseDTO result = projectService.findById(projectId);

        assertNotNull(result);
        assertEquals(projectId, result.id());
        assertEquals("Test Project", result.title());
    }

    @Test
    void create_ShouldReturnCreatedProject() {
        ProjectRequestDTO request = new ProjectRequestDTO("New Project", "New Description", "creator", "IN_EDITING");
        when(userRepository.findByUsername("creator")).thenReturn(Optional.of(creator));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponseDTO result = projectService.create(request);

        assertNotNull(result);
        assertEquals("Test Project", result.title());
    }
}
