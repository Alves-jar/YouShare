package com.noxus.youshare.dto.project;

public record ProjectRequestDTO(
    String title,
    String description,
    String creatorUsername,
    String status
) {
}
