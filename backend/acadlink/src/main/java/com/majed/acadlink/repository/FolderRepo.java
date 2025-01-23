package com.majed.acadlink.repository;

import com.majed.acadlink.entitie.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FolderRepo extends JpaRepository<Folder, UUID> {
    List<Folder> findByUserId(UUID id);
}
