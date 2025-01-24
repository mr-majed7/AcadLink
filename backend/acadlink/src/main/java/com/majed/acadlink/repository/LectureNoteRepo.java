package com.majed.acadlink.repository;

import com.majed.acadlink.entitie.LectureNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LectureNoteRepo extends JpaRepository<LectureNote, UUID> {
}
