package com.noxus.youshare.controller;

import com.noxus.youshare.dto.video.VideoResponseDTO;
import com.noxus.youshare.entity.Video;
import com.noxus.youshare.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videos")
@AllArgsConstructor
@Tag(name = "Videos", description = "Endpoints for video management")
public class VideoController {

    private final VideoService videoService;

    @Operation(summary = "Upload video", description = "Uploads a video to a project")
    @PostMapping("/projects/{projectId}")
    public ResponseEntity<VideoResponseDTO> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Project ID", required = true) @PathVariable UUID projectId,
            @RequestParam(required = false) String description,
            @AuthenticationPrincipal com.noxus.youshare.entity.User user) {

        Video video = videoService.uploadVideo(file, projectId, user, description);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(video));
    }

    @Operation(summary = "List project videos", description = "Returns all videos from a specific project")
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<VideoResponseDTO>> getProjectVideos(
            @Parameter(description = "Project ID", required = true) @PathVariable UUID projectId) {
        List<Video> videos = videoService.findByProject(projectId);
        List<VideoResponseDTO> dtos = videos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<VideoResponseDTO> getVideo(@PathVariable UUID videoId) {
        Video video = videoService.findById(videoId);
        return ResponseEntity.ok(toDTO(video));
    }

    @GetMapping("/{videoId}/download")
    public ResponseEntity<Resource> downloadVideo(@PathVariable UUID videoId) {
        Video video = videoService.findById(videoId);
        byte[] content = videoService.getVideoContent(videoId);
        
        ByteArrayResource resource = new ByteArrayResource(content);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(video.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + video.getOriginalFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<Void> deleteVideo(
            @PathVariable UUID videoId,
            @AuthenticationPrincipal com.noxus.youshare.entity.User user) {
        
        videoService.deleteVideo(videoId, user);
        
        return ResponseEntity.noContent().build();
    }

    private VideoResponseDTO toDTO(Video video) {
        return new VideoResponseDTO(
                video.getId(),
                video.getFilename(),
                video.getOriginalFilename(),
                video.getContentType(),
                video.getFileSize(),
                video.getDescription(),
                video.getProject().getId(),
                video.getProject().getTitle(),
                video.getUploadedBy().getId(),
                video.getUploadedBy().getUsername(),
                video.getUploadedAt()
        );
    }
}
