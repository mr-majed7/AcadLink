package com.majed.acadlink.domain.repository;

import com.majed.acadlink.domain.entitie.Materials;
import com.majed.acadlink.enums.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MaterialsRepo extends JpaRepository<Materials, UUID> {
    List<Materials> findByFolderIdAndType(UUID id, MaterialType type);

    List<Materials> findByType(MaterialType type);
}
