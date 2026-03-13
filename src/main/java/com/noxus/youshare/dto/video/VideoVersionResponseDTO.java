package com.noxus.youshare.dto.video;

import com.noxus.youshare.entity.enums.VersionStatus;
import java.util.Date;
import java.util.UUID;

public record VideoVersionResponseDTO(
        UUID id,
        UUID videoId,
        Integer versionNumber,
        String filename,
        String originalFilename,
        String contentType,
        Long fileSize,
        String description,
        VersionStatus status,
        UUID uploadedById,
        String uploadedByUsername,
        Date uploadedAt,
        UUID approvedById,
        String approvedByUsername,
        Date approvedAt,
        String rejectionReason
) {}
