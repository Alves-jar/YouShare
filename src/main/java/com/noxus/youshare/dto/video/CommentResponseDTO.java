package com.noxus.youshare.dto.video;

import com.noxus.youshare.entity.Comment;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class CommentResponseDTO {

    private UUID id;
    private UUID versionId;
    private UUID userId;
    private String username;
    private String content;
    private Date createdAt;

    public static CommentResponseDTO fromEntity(Comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setVersionId(comment.getVersion().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
