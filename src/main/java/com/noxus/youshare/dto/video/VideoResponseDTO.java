package com.noxus.youshare.dto.video;

import java.util.Date;
import java.util.UUID;

public record VideoResponseDTO(
        UUID id,
        String filename,
        String originalFilename,
        String contentType,
        Long fileSize,
        String description,
        UUID projectId,
        String projectTitle,
        UUID uploadedById,
        String uploadedByUsername,
        Date uploadedAt
) {}
