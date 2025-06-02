package com.majed.acadlink.domain.repository;

import com.majed.acadlink.domain.entity.Peers;
import com.majed.acadlink.enums.PeerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PeersRepo extends JpaRepository<Peers, UUID> {
    Peers findByUser1IdAndUser2IdOrUser2IdAndUser1Id(UUID user1Id, UUID user2Id, UUID user2IdAlt, UUID user1IdAlt);

    List<Peers> findByUser1IdAndStatus(UUID user1Id, PeerStatus status);

    List<Peers> findByUser2IdAndStatus(UUID user1Id, PeerStatus status);

    List<Peers> findByUser1IdOrUser2IdAndStatus(UUID userId1, UUID userId2, PeerStatus status);
}
