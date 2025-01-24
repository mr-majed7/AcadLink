package com.majed.acadlink.repository;

import com.majed.acadlink.entitie.Other;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OtherRepo extends JpaRepository<Other, UUID> {
    Optional<List<Other>> findByFolderId(UUID folderId);
}
