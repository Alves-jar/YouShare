package com.noxus.youshare.entity.enums;

public enum UserRole {
    EDITOR,
    CREATOR;

    public static UserRole fromString(String role) {
        return switch (role.toLowerCase()) {
            case "editor" -> EDITOR;
            case "creator" -> CREATOR;
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };
    }
}
