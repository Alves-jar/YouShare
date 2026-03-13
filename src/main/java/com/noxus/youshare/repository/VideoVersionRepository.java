package com.noxus.youshare.repository;

import com.noxus.youshare.entity.VideoVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoVersionRepository extends JpaRepository<VideoVersion, UUID> {
    
    List<VideoVersion> findByVideoId(UUID videoId);
    
    List<VideoVersion> findByVideoIdOrderByVersionNumberDesc(UUID videoId);
    
    Optional<VideoVersion> findByVideoIdAndVersionNumber(UUID videoId, Integer versionNumber);
    
    Optional<VideoVersion> findTopByVideoIdOrderByVersionNumberDesc(UUID videoId);
    
    @Query("SELECT MAX(v.versionNumber) FROM VideoVersion v WHERE v.video.id = :videoId")
    Integer findMaxVersionNumberByVideoId(UUID videoId);
    
    List<VideoVersion> findByUploadedById(UUID uploadedById);
    
    List<VideoVersion> findByStatus(com.noxus.youshare.entity.enums.VersionStatus status);
}
