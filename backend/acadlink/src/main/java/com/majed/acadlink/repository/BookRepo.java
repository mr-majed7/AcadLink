package com.majed.acadlink.repository;

import com.majed.acadlink.entitie.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepo extends JpaRepository<Book, UUID> {
    Optional<List<Book>> findByFolderId(UUID folderId);
}
