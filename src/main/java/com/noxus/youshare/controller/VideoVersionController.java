package com.noxus.youshare.controller;

import com.noxus.youshare.dto.video.VideoVersionResponseDTO;
import com.noxus.youshare.entity.VideoVersion;
import com.noxus.youshare.service.VideoVersionService;
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
@RequestMapping("/api/video-versions")
@AllArgsConstructor
public class VideoVersionController {

    private final VideoVersionService videoVersionService;

    @PostMapping("/videos/{videoId}")
    public ResponseEntity<VideoVersionResponseDTO> uploadNewVersion(
            @RequestParam("file") MultipartFile file,
            @PathVariable UUID videoId,
            @RequestParam(required = false) String description,
            @AuthenticationPrincipal com.noxus.youshare.entity.User user) {

        VideoVersion version = videoVersionService.uploadNewVersion(file, videoId, user, description);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(version));
    }

    @GetMapping("/videos/{videoId}")
    public ResponseEntity<List<VideoVersionResponseDTO>> getVideoVersions(@PathVariable UUID videoId) {
        List<VideoVersion> versions = videoVersionService.getVideoVersions(videoId);
        List<VideoVersionResponseDTO> dtos = versions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{versionId}")
    public ResponseEntity<VideoVersionResponseDTO> getVersion(@PathVariable UUID versionId) {
        VideoVersion version = videoVersionService.getVersion(versionId);
        return ResponseEntity.ok(toDTO(version));
    }

    @GetMapping("/{versionId}/download")
    public ResponseEntity<Resource> downloadVersion(@PathVariable UUID versionId) {
        VideoVersion version = videoVersionService.getVersion(versionId);
        byte[] content = videoVersionService.getVersionContent(versionId);
        
        ByteArrayResource resource = new ByteArrayResource(content);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(version.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + version.getOriginalFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/{versionId}/approve")
    public ResponseEntity<VideoVersionResponseDTO> approveVersion(
            @PathVariable UUID versionId,
            @AuthenticationPrincipal com.noxus.youshare.entity.User user) {
        
        VideoVersion version = videoVersionService.approveVersion(versionId, user);
        
        return ResponseEntity.ok(toDTO(version));
    }

    @PostMapping("/{versionId}/reject")
    public ResponseEntity<VideoVersionResponseDTO> rejectVersion(
            @PathVariable UUID versionId,
            @RequestParam String reason,
            @AuthenticationPrincipal com.noxus.youshare.entity.User user) {
        
        VideoVersion version = videoVersionService.rejectVersion(versionId, user, reason);
        
        return ResponseEntity.ok(toDTO(version));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VideoVersionResponseDTO>> getVersionsByUser(@PathVariable UUID userId) {
        List<VideoVersion> versions = videoVersionService.getVersionsByUser(userId);
        List<VideoVersionResponseDTO> dtos = versions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<VideoVersionResponseDTO>> getPendingVersions() {
        List<VideoVersion> versions = videoVersionService.getPendingVersions();
        List<VideoVersionResponseDTO> dtos = versions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    private VideoVersionResponseDTO toDTO(VideoVersion version) {
        String approvedByUsername = version.getApprovedBy() != null ? 
            version.getApprovedBy().getUsername() : null;
        
        return new VideoVersionResponseDTO(
                version.getId(),
                version.getVideo().getId(),
                version.getVersionNumber(),
                version.getFilename(),
                version.getOriginalFilename(),
                version.getContentType(),
                version.getFileSize(),
                version.getDescription(),
                version.getStatus(),
                version.getUploadedBy().getId(),
                version.getUploadedBy().getUsername(),
                version.getUploadedAt(),
                version.getApprovedBy() != null ? version.getApprovedBy().getId() : null,
                approvedByUsername,
                version.getApprovedAt(),
                version.getRejectionReason()
        );
    }
}
