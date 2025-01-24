package com.majed.acadlink.repository;

import com.majed.acadlink.entitie.LectureNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LectureNoteRepo extends JpaRepository<LectureNote, UUID> {
    Optional<List<LectureNote>> findByFolderId(UUID folderId);
}
