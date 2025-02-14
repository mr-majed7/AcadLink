package com.majed.acadlink.domain.repository;

import com.majed.acadlink.domain.entitie.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FolderRepo extends JpaRepository<Folder, UUID> {
    List<Folder> findByUserId(UUID id);
}
