package com.noxus.youshare.entity.enums;

public enum MemberRole {
    VIEWER,
    EDITOR,
    ADMIN;

    public static MemberRole fromString(String role) {
        return switch (role.toLowerCase()) {
            case "viewer" -> VIEWER;
            case "editor" -> EDITOR;
            case "admin"  -> ADMIN;
            default -> throw new IllegalArgumentException("Invalid member role: " + role);
        };
    }
}