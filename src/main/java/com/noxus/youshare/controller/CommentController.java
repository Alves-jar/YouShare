package com.noxus.youshare.controller;

import com.noxus.youshare.dto.video.CommentRequestDTO;
import com.noxus.youshare.dto.video.CommentResponseDTO;
import com.noxus.youshare.entity.Comment;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/versions")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{versionId}/comments")
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable UUID versionId,
            @Valid @RequestBody CommentRequestDTO requestDTO,
            @AuthenticationPrincipal User user) {

        Comment comment = commentService.createComment(versionId, user, requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommentResponseDTO.fromEntity(comment));
    }

    @GetMapping("/{versionId}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByVersion(
            @PathVariable UUID versionId) {

        List<Comment> comments = commentService.getCommentsByVersion(versionId);
        List<CommentResponseDTO> dtos = comments.stream()
                .map(CommentResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
