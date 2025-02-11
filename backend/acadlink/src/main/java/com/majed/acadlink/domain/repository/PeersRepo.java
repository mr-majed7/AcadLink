package com.majed.acadlink.domain.repository;

import com.majed.acadlink.domain.entitie.Peers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PeersRepo extends JpaRepository<Peers, UUID> {
    Peers findByUser1IdAndUser2IdOrUser2IdAndUser1Id(UUID user1Id, UUID user2Id, UUID user2IdAlt, UUID user1IdAlt);
}
