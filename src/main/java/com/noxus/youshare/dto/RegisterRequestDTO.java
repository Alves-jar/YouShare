package com.noxus.youshare.dto;

public record RegisterRequestDTO(
    String username,
    String email,
    String password,
    String role
) {
}
