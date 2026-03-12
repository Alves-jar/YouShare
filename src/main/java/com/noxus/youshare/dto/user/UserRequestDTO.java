package com.noxus.youshare.dto.user;

public record UserRequestDTO(
    String username,
    String email,
    String password,
    String role
) {}
