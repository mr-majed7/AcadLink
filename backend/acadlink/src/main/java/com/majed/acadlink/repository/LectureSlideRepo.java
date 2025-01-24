package com.majed.acadlink.repository;

import com.majed.acadlink.entitie.LectureSlide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LectureSlideRepo extends JpaRepository<LectureSlide, UUID> {
    Optional<List<LectureSlide>> findByFolderId(UUID folderId);
}
