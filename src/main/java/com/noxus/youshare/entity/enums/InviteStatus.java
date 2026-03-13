package com.noxus.youshare.entity.enums;

public enum InviteStatus {
    INVITED,
    ACCEPTED;

    public static InviteStatus fromString(String status) {
        return switch (status.toLowerCase()) {
            case "invited"  -> INVITED;
            case "accepted" -> ACCEPTED;
            default -> throw new IllegalArgumentException("Invalid invite status: " + status);
        };
    }
}