package com.noxus.youshare.dto.notification;

import com.noxus.youshare.entity.Notification;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class NotificationResponseDTO {

    private UUID id;
    private UUID userId;
    private String type;
    private String title;
    private String message;
    private UUID projectId;
    private UUID versionId;
    private boolean read;
    private Date createdAt;

    public static NotificationResponseDTO fromEntity(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setType(notification.getType().name());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setProjectId(notification.getProjectId());
        dto.setVersionId(notification.getVersionId());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
