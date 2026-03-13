package com.noxus.youshare.repository;

import com.noxus.youshare.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByVersionIdOrderByCreatedAtDesc(UUID versionId);
}
