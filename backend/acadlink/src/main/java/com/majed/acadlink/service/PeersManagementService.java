package com.majed.acadlink.service;

import com.majed.acadlink.domain.entity.Peers;
import com.majed.acadlink.domain.entity.User;
import com.majed.acadlink.domain.repository.PeersRepo;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.peers.PeerInfoDTO;
import com.majed.acadlink.dto.peers.SearchResultDTO;
import com.majed.acadlink.enums.PeerStatus;
import com.majed.acadlink.enums.ReqType;
import com.majed.acadlink.utility.AuthorizationCheck;
import com.majed.acadlink.utility.GetUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing peers.
 */
@Service
@Slf4j
public class PeersManagementService {
    private final UserRepo userRepo;
    private final PeersRepo peersRepo;
    private final GetUserUtil getUserUtil;
    private final AuthorizationCheck authorizationCheck;

    /**
     * Constructor for PeersManagementService.
     *
     * @param peersRepo          the peers repository
     * @param userRepo           the user repository
     * @param getUserUtil        utility to get the authenticated user
     * @param authorizationCheck utility to check user authorization
     */
    public PeersManagementService(
            PeersRepo peersRepo,
            UserRepo userRepo,
            GetUserUtil getUserUtil,
            AuthorizationCheck authorizationCheck) {
        this.peersRepo = peersRepo;
        this.userRepo = userRepo;
        this.getUserUtil = getUserUtil;
        this.authorizationCheck = authorizationCheck;
    }

    /**
     * Searches for users based on the provided entry.
     *
     * @param entry the search entry
     * @return the response entity containing the search results or an error status
     */
    public ResponseEntity<ApiResponse<List<SearchResultDTO>>> searchUsers(String entry) {
        Optional<User> currentUser = getUserUtil.getAuthenticatedUser();
        if (currentUser.isEmpty()) {
            return ApiResponse.error("User not found", HttpStatus.BAD_REQUEST);
        }
        List<User> userList = userRepo.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(entry, entry);
        List<SearchResultDTO> users = userList.stream().map(user -> {
            PeerStatus peerStatus = getPeerStatus(currentUser.get().getId(), user.getId());
            return new SearchResultDTO(user.getId(), user.getFirstName(), user.getLastName(),
                    user.getEmail(), user.getUsername(), user.getInstitute(), peerStatus);
        }).toList();
        return ApiResponse.success(users, HttpStatus.OK);
    }

    /**
     * Gets the peer status between two users.
     *
     * @param currentUserId the ID of the current user
     * @param otherUserId   the ID of the other user
     * @return the peer status
     */
    private PeerStatus getPeerStatus(UUID currentUserId, UUID otherUserId) {
        Peers peer = peersRepo.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(currentUserId, otherUserId, currentUserId, otherUserId);

        if (peer != null) {
            return peer.getStatus();
        }

        return PeerStatus.FALSE;
    }

    /**
     * Sends a peer request to the specified user.
     *
     * @param user2Id the ID of the user to send the peer request to
     * @return the response entity containing the status of the request or an error status
     */
    public ResponseEntity<ApiResponse<Boolean>> addPeer(UUID user2Id) {
        Optional<User> currentUser = getUserUtil.getAuthenticatedUser();
        if (currentUser.isEmpty()) {
            return ApiResponse.error("User not found", HttpStatus.BAD_REQUEST);
        }
        UUID user1Id = currentUser.get().getId();

        PeerStatus peerStatus = getPeerStatus(user1Id, user2Id);

        switch (peerStatus) {
            case FALSE -> {
                Peers peers = new Peers();
                Optional<User> user2 = userRepo.findById(user2Id);
                if (user2.isEmpty()) {
                    return ApiResponse.error("User2 not found", HttpStatus.BAD_REQUEST);
                }

                peers.setUser1(currentUser.get());
                peers.setUser2(user2.get());
                try {
                    peersRepo.save(peers);
                    return ApiResponse.success(true, HttpStatus.CREATED);
                } catch (Exception e) {
                    log.error(e.toString());
                    return ApiResponse.error("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            case PENDING -> {
                return ApiResponse.error("Request already sent", HttpStatus.BAD_REQUEST);
            }
            default -> {
                return ApiResponse.error("Already a peer", HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * Retrieves peer requests based on the specified type.
     *
     * @param type the type of the peer requests to retrieve
     * @return the response entity containing the list of peer requests or an error status
     */
    public ResponseEntity<ApiResponse<List<PeerInfoDTO>>> getRequests(ReqType type) {
        Optional<User> currentUser = getUserUtil.getAuthenticatedUser();
        if (currentUser.isEmpty()) {
            return ApiResponse.error("User not found", HttpStatus.BAD_REQUEST);
        }
        UUID userId = currentUser.get().getId();

        List<Peers> requests = new ArrayList<>();
        if (type == ReqType.SENT) {
            requests = peersRepo.findByUser1IdAndStatus(userId, PeerStatus.PENDING);
        } else if (type == ReqType.RECEIVED) {
            requests = peersRepo.findByUser2IdAndStatus(userId, PeerStatus.PENDING);
        }
        List<PeerInfoDTO> requestList = requests.stream().map(req -> {
            User targetUser = (type == ReqType.SENT) ? req.getUser2() : req.getUser1();
            User user = userRepo.findById(targetUser.getId()).get();
            return new PeerInfoDTO(req.getId(), req.getUser2().getId(), user.getFirstName()
                    , user.getLastName(), user.getEmail(), user.getInstitute(), user.getUsername()
            );
        }).toList();

        return ApiResponse.success(requestList, HttpStatus.OK);
    }

    /**
     * Retrieves the list of peers for the authenticated user.
     *
     * @return the response entity containing the list of peers or an error status
     */
    public ResponseEntity<ApiResponse<List<PeerInfoDTO>>> findPeers() {
        Optional<User> currentUser = getUserUtil.getAuthenticatedUser();
        if (currentUser.isEmpty()) {
            return ApiResponse.error("User not found", HttpStatus.BAD_REQUEST);
        }
        UUID userId = currentUser.get().getId();

        List<Peers> peersList = peersRepo.findByUser1IdOrUser2IdAndStatus(userId, userId, PeerStatus.ACCEPTED);

        List<PeerInfoDTO> peerList = peersList.stream().map(peer -> {
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
        return ApiResponse.success(peerList, HttpStatus.OK);
    }

    /**
     * Accepts a peer request with the specified request ID.
     *
     * @param reqId the ID of the peer request to accept
     * @return the response entity containing the status of the acceptance or an error status
     */
    public ResponseEntity<ApiResponse<Boolean>> acceptRequest(UUID reqId) {
        Optional<Peers> peer = peersRepo.findById(reqId);
        if (peer.isEmpty()) {
            return ApiResponse.error("Peer not found", HttpStatus.NOT_FOUND);
        }
        User user1 = peer.get().getUser1();
        User user2 = peer.get().getUser2();
        if (authorizationCheck.checkAuthorization(user1.getId()) || authorizationCheck.checkAuthorization(user2.getId())) {
            peer.get().setStatus(PeerStatus.ACCEPTED);
            peersRepo.save(peer.get());
            return ApiResponse.success(true, HttpStatus.OK);
        } else {
            return ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Removes a peer with the specified peer ID.
     *
     * @param peerId the ID of the peer to remove
     * @return the response entity containing the status of the removal or an error status
     */
    public ResponseEntity<ApiResponse<Boolean>> removePeer(UUID peerId) {
        Optional<Peers> peer = peersRepo.findById(peerId);
        if (peer.isEmpty()) {
            return ApiResponse.error("Peer not found", HttpStatus.NOT_FOUND);
        } else {
            User user1 = peer.get().getUser1();
            User user2 = peer.get().getUser2();
            if (authorizationCheck.checkAuthorization(user1.getId()) ||
                    authorizationCheck.checkAuthorization(user2.getId())) {
                peersRepo.delete(peer.get());
                return ApiResponse.success(true, HttpStatus.OK);
            } else {
                return ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
            }
        }
    }
}
