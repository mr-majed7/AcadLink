package com.majed.acadlink.repository;

import com.majed.acadlink.entitie.Materials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MaterialsRepo extends JpaRepository<Materials, UUID> {
    List<Materials> findByFolderId(UUID id);
}
