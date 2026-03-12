package com.noxus.youshare.dto.auth;

public record RegisterRequestDTO(
    String username,
    String email,
    String password,
    String role
) {
}
