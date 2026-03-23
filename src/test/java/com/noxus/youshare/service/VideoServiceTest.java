package com.noxus.youshare.service;

import com.noxus.youshare.entity.Video;
import com.noxus.youshare.entity.Project;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.entity.enums.ProjectStatus;
import com.noxus.youshare.entity.enums.UserRole;
import com.noxus.youshare.repository.VideoRepository;
import com.noxus.youshare.repository.ProjectRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private VideoService videoService;

    private Video video;
    private Project project;
    private UUID videoId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        videoId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        User creator = new User();
        creator.setId(UUID.randomUUID());
        creator.setUsername("creator");
        creator.setRole(UserRole.CREATOR);

        project = new Project();
        project.setId(projectId);
        project.setTitle("Test Project");
        project.setCreator(creator);
        project.setStatus(ProjectStatus.IN_EDITING);

        video = new Video();
        video.setId(videoId);
        video.setFilename("test-video.mp4");
        video.setOriginalFilename("test-video.mp4");
        video.setContentType("video/mp4");
        video.setFileSize(1024L);
        video.setFilePath("/uploads/test.mp4");
        video.setDescription("Test Description");
        video.setProject(project);
        video.setUploadedBy(creator);
        video.setUploadedAt(new Date());
    }

    @Test
    void findByProjectId_ShouldReturnListOfVideos() {
        when(videoRepository.findByProjectId(projectId)).thenReturn(List.of(video));

        var result = videoRepository.findByProjectId(projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-video.mp4", result.get(0).getFilename());
    }

    @Test
    void findById_ShouldReturnVideo() {
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));

        Optional<Video> result = videoRepository.findById(videoId);

        assertTrue(result.isPresent());
        assertEquals(videoId, result.get().getId());
    }
}
