package com.majed.acadlink.service;

import com.majed.acadlink.domain.entitie.Peers;
import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.PeersRepo;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.peers.SearchResultDTO;
import com.majed.acadlink.enums.PeerStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PeersManagementService {
    @Autowired
    private PeersRepo peersRepo;

    @Autowired
    private UserRepo userRepo;

    public List<SearchResultDTO> searchUsers(String entry, UUID currentUser) {
        List<User> userList = userRepo.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(entry, entry);
        return userList.stream().map(user -> {
            PeerStatus peerStatus = getPeerStatus(currentUser, user.getId());
            return new SearchResultDTO(user.getId(), user.getFirstName(), user.getLastName(),
                    user.getEmail(), user.getUsername(), user.getInstitute(), peerStatus);
        }).collect(Collectors.toList());
    }


    private PeerStatus getPeerStatus(UUID currentUserId, UUID otherUserId) {
        Peers peer = peersRepo.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(currentUserId, otherUserId, currentUserId, otherUserId);

        if (peer != null) {
            return peer.getStatus();
        }

        return PeerStatus.FALSE;
    }
}
