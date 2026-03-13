package com.noxus.youshare.repository;

import com.noxus.youshare.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {
    
    List<Video> findByProjectId(UUID projectId);
    
    List<Video> findByUploadedById(UUID uploadedById);
    
    void deleteByProjectId(UUID projectId);
}
