package com.majed.acadlink.repository;

import com.majed.acadlink.entitie.Other;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OtherRepo extends JpaRepository<Other, UUID> {
}
