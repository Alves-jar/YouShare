package com.noxus.youshare.dto.project;

import com.noxus.youshare.dto.user.UserResponseDTO;
import com.noxus.youshare.entity.enums.ProjectStatus;

import java.util.Date;
import java.util.UUID;

public record ProjectResponseDTO(
    UUID id,
    String title,
    String description,
    UserResponseDTO creator,
    ProjectStatus status,
    Date createdAt
) {
}
