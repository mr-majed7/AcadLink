package com.majed.acadlink.repository;

import com.majed.acadlink.entitie.LectureSlide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LectureSlideRepo extends JpaRepository<LectureSlide, UUID> {
}
