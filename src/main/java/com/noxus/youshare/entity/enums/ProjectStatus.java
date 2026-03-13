package com.noxus.youshare.entity.enums;

public enum ProjectStatus {
    OPEN,
    IN_EDITING,
    REVIEW,
    COMPLETED;

    public static ProjectStatus fromString(String status) {
        return switch (status.toLowerCase()) {
            case "open" -> OPEN;
            case "in_editing" -> IN_EDITING;
            case "review" -> REVIEW;
            case "completed" -> COMPLETED;
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        };
    }
}
