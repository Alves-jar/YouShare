package com.noxus.youshare.service;

import com.noxus.youshare.entity.Project;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.entity.Video;
import com.noxus.youshare.exception.ProjectNotFoundException;
import com.noxus.youshare.exception.UnauthorizedException;
import com.noxus.youshare.exception.VideoNotFoundException;
import com.noxus.youshare.repository.ProjectRepository;
import com.noxus.youshare.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Value("${app.video.upload-dir:uploads/videos}")
    private String uploadDir;

    @Value("${app.video.max-size:104857600}")
    private long maxFileSize;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "video/mp4",
        "video/avi",
        "video/mov",
        "video/wmv",
        "video/flv",
        "video/webm",
        "video/mkv"
    );

    public Video uploadVideo(MultipartFile file, UUID projectId, User uploadedBy, String description) {
        validateFile(file);

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        if (!isUserAuthorized(uploadedBy, project)) {
            throw new UnauthorizedException("User not authorized to upload videos to this project");
        }

        try {
            String filename = generateUniqueFilename(file.getOriginalFilename());
            Path filePath = createUploadDirectory().resolve(filename);
            
            Files.copy(file.getInputStream(), filePath);

            Video video = new Video();
            video.setId(null);
            video.setFilename(filename);
            video.setOriginalFilename(file.getOriginalFilename());
            video.setContentType(file.getContentType());
            video.setFileSize(file.getSize());
            video.setFilePath(filePath.toString());
            video.setDescription(description);
            video.setProject(project);
            video.setUploadedBy(uploadedBy);
            video.setUploadedAt(null);

            return videoRepository.save(video);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store video file", e);
        }
    }

    public Video findById(UUID videoId) {
        return videoRepository.findById(videoId)
            .orElseThrow(() -> new VideoNotFoundException("Video not found"));
    }

    public List<Video> findByProject(UUID projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        
        return videoRepository.findByProjectId(projectId);
    }

    public void deleteVideo(UUID videoId, User user) {
        Video video = findById(videoId);

        if (!isUserAuthorized(user, video.getProject())) {
            throw new UnauthorizedException("User not authorized to delete this video");
        }

        try {
            Files.deleteIfExists(Paths.get(video.getFilePath()));
            videoRepository.delete(video);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete video file", e);
        }
    }

    public byte[] getVideoContent(UUID videoId) {
        Video video = findById(videoId);
        
        try {
            return Files.readAllBytes(Paths.get(video.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read video file", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid video format. Allowed formats: " + ALLOWED_CONTENT_TYPES);
        }
    }

    private boolean isUserAuthorized(User user, Project project) {
        return project.getCreator().getId().equals(user.getId());
    }

    private String generateUniqueFilename(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }

    private Path createUploadDirectory() throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        return uploadPath;
    }
}
