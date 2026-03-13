package com.noxus.youshare.service;

import com.noxus.youshare.entity.Notification;
import com.noxus.youshare.entity.Project;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.entity.VideoVersion;
import com.noxus.youshare.entity.enums.NotificationType;
import com.noxus.youshare.exception.NotificationNotFoundException;
import com.noxus.youshare.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public Notification createEditorInviteNotification(User invitedUser, Project project, User invitedBy) {
        Notification notification = new Notification();
        notification.setUser(invitedUser);
        notification.setType(NotificationType.EDITOR_INVITE);
        notification.setTitle("Convite para projeto");
        notification.setMessage("Você foi convidado como editor no projeto '" + project.getTitle() + "' por " + invitedBy.getUsername());
        notification.setProjectId(project.getId());
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification createNewVersionNotification(User projectCreator, VideoVersion version, User uploadedBy) {
        Project project = version.getVideo().getProject();
        Notification notification = new Notification();
        notification.setUser(projectCreator);
        notification.setType(NotificationType.NEW_VERSION);
        notification.setTitle("Nova versão de vídeo");
        notification.setMessage(uploadedBy.getUsername() + " enviou uma nova versão (v" + version.getVersionNumber() + ") no projeto '" + project.getTitle() + "'");
        notification.setProjectId(project.getId());
        notification.setVersionId(version.getId());
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification createNewCommentNotification(User versionUploader, VideoVersion version, User commenter) {
        Project project = version.getVideo().getProject();
        Notification notification = new Notification();
        notification.setUser(versionUploader);
        notification.setType(NotificationType.NEW_COMMENT);
        notification.setTitle("Novo comentário");
        notification.setMessage(commenter.getUsername() + " comentou na versão v" + version.getVersionNumber() + " do projeto '" + project.getTitle() + "'");
        notification.setProjectId(project.getId());
        notification.setVersionId(version.getId());
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public Notification markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to notification");
        }
        
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
