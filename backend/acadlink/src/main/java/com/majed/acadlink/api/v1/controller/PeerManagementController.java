package com.majed.acadlink.api.v1.controller;

import com.majed.acadlink.domain.entitie.Peers;
import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.PeersRepo;
import com.majed.acadlink.dto.peers.PeerInfoDTO;
import com.majed.acadlink.dto.peers.SearchResultDTO;
import com.majed.acadlink.enums.PeerStatus;
import com.majed.acadlink.enums.ReqType;
import com.majed.acadlink.service.PeersManagementService;
import com.majed.acadlink.utility.AuthorizationCheck;
import com.majed.acadlink.utility.GetUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("peers")
@Slf4j
@Tag(name = "5. Peer Management", description = "Endpoints to manage peers and search for users")
public class PeerManagementController {
    @Autowired
    private PeersRepo peersRepo;

    @Autowired
    private AuthorizationCheck authorizationCheck;

    @Autowired
    private PeersManagementService peersManagementService;

    @Autowired
    private GetUserUtil getUserUtil;

    @Operation(summary = "Search users", tags = {"5. Peer Management"})
    @GetMapping("search-user/{entry}")
    public ResponseEntity<List<SearchResultDTO>> searchUsers(@PathVariable String entry) {
        Optional<User> currentUser = getUserUtil.getAuthenticatedUser();
        List<SearchResultDTO> users = peersManagementService.searchUsers(entry, currentUser.get().getId());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Send peer request", tags = {"5. Peer Management"})
    @PostMapping("send-peer-request/{userId}")
    public ResponseEntity<Boolean> addPeer(@PathVariable UUID userId) {
        Optional<User> currentUser = getUserUtil.getAuthenticatedUser();
        if (peersManagementService.addPeer(currentUser.get().getId(), userId)) {
            return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get peer requests", tags = {"5. Peer Management"})
    @GetMapping("get-peer-requests/{type}")
    public ResponseEntity<List<PeerInfoDTO>> getRequests(@PathVariable ReqType type) {
        Optional<User> currentUser = getUserUtil.getAuthenticatedUser();
        List<PeerInfoDTO> requestList = peersManagementService.getRequests(currentUser.get().getId(), type);
        return new ResponseEntity<>(requestList, HttpStatus.OK);
    }

    @Operation(summary = "Accept peer request", tags = {"5. Peer Management"})
    @PutMapping("accept-peer-request/{reqId}")
    public ResponseEntity<Boolean> acceptRequest(@PathVariable UUID reqId) {
        Optional<Peers> request = peersRepo.findById(reqId);
        if (request.isEmpty()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        } else if (request.get().getStatus() != PeerStatus.PENDING) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        } else {
            if (authorizationCheck.checkAuthorization(request.get().getUser2().getId())) {
                request.get().setStatus(PeerStatus.ACCEPTED);
                peersRepo.save(request.get());
                return new ResponseEntity<>(true, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(false, HttpStatus.FORBIDDEN);
            }
        }
    }

    @Operation(summary = "Get peers", tags = {"5. Peer Management"})
    @GetMapping("get-peers")
    public ResponseEntity<List<PeerInfoDTO>> getPeers() {
        User currentUser = getUserUtil.getAuthenticatedUser().get();
        List<PeerInfoDTO> peersList = peersManagementService.findPeers(currentUser.getId());
        return new ResponseEntity<>(peersList, HttpStatus.OK);
    }

    @Operation(summary = "Remove peer", tags = {"5. Peer Management"})
    @DeleteMapping("remove-peer/{peerId}")
    public ResponseEntity<Boolean> removePeer(@PathVariable UUID peerId) {
        Optional<Peers> peer = peersRepo.findById(peerId);
        if (peer.isEmpty()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        } else {
            User user1 = peer.get().getUser1();
            User user2 = peer.get().getUser2();
            if (authorizationCheck.checkAuthorization(user1.getId()) ||
                    authorizationCheck.checkAuthorization(user2.getId())) {
                peersRepo.delete(peer.get());
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.FORBIDDEN);
            }
        }
    }
}


