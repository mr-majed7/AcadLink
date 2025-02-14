package com.majed.acadlink.service;

import com.majed.acadlink.domain.entitie.Peers;
import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.PeersRepo;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.peers.PeerInfoDTO;
import com.majed.acadlink.dto.peers.SearchResultDTO;
import com.majed.acadlink.enums.PeerStatus;
import com.majed.acadlink.enums.ReqType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PeersManagementService {
    private final PeersRepo peersRepo;
    private final UserRepo userRepo;

    public PeersManagementService(PeersRepo peersRepo, UserRepo userRepo) {
        this.peersRepo = peersRepo;
        this.userRepo = userRepo;
    }

    public List<SearchResultDTO> searchUsers(String entry, UUID currentUser) {
        List<User> userList = userRepo.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(entry, entry);
        return userList.stream().map(user -> {
            PeerStatus peerStatus = getPeerStatus(currentUser, user.getId());
            return new SearchResultDTO(user.getId(), user.getFirstName(), user.getLastName(),
                    user.getEmail(), user.getUsername(), user.getInstitute(), peerStatus);
        }).toList();
    }


    private PeerStatus getPeerStatus(UUID currentUserId, UUID otherUserId) {
        Peers peer = peersRepo.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(currentUserId, otherUserId, currentUserId, otherUserId);

        if (peer != null) {
            return peer.getStatus();
        }

        return PeerStatus.FALSE;
    }

    public boolean addPeer(UUID user1Id, UUID user2Id) {
        PeerStatus peerStatus = getPeerStatus(user1Id, user2Id);

        if (peerStatus == PeerStatus.FALSE) {
            Peers peers = new Peers();
            User user1 = userRepo.findById(user1Id).get();
            User user2 = userRepo.findById(user2Id).get();
            peers.setUser1(user1);
            peers.setUser2(user2);
            try {
                peersRepo.save(peers);
                return true;
            } catch (Exception e) {
                log.error(e.toString());

                return false;
            }
        } else {
            return false;
        }
    }

    public List<PeerInfoDTO> getRequests(UUID userId, ReqType type) {
        List<Peers> requests = new ArrayList<>();
        if (type == ReqType.SENT) {
            requests = peersRepo.findByUser1IdAndStatus(userId, PeerStatus.PENDING);
        } else if (type == ReqType.RECEIVED) {
            requests = peersRepo.findByUser2IdAndStatus(userId, PeerStatus.PENDING);
        }
        return requests.stream().map(req -> {
            User targetUser = (type == ReqType.SENT) ? req.getUser2() : req.getUser1();
            User user = userRepo.findById(targetUser.getId()).get();
            return new PeerInfoDTO(req.getId(), req.getUser2().getId(), user.getFirstName()
                    , user.getLastName(), user.getEmail(), user.getInstitute(), user.getUsername()
            );
        }).toList();
    }

    public List<PeerInfoDTO> findPeers(UUID userId) {
        List<Peers> peersList = peersRepo.findByUser1IdOrUser2IdAndStatus(userId, userId, PeerStatus.ACCEPTED);

        return peersList.stream().map(peer -> {
            User user;
            if (peer.getUser1().getId().equals(userId)) {
                user = peer.getUser2();
            } else {
                user = peer.getUser1();
            }
            return new PeerInfoDTO(peer.getId(), user.getId(), user.getFirstName()
                    , user.getLastName(), user.getEmail(), user.getInstitute(), user.getUsername()
            );
        }).toList();
    }
}
