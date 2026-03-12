package com.noxus.youshare.dto.user;

import com.noxus.youshare.entity.enums.UserRole;

import java.util.UUID;

public record UserResponseDTO(
    UUID id,
    String username,
    String email,
    UserRole role
) {}
