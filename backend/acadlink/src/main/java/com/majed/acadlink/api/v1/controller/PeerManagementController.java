package com.majed.acadlink.api.v1.controller;

import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.peers.PeerInfoDTO;
import com.majed.acadlink.dto.peers.SearchResultDTO;
import com.majed.acadlink.enums.ReqType;
import com.majed.acadlink.service.PeersManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


/**
 * Controller for managing peers and searching for users.
 */
@RestController
@RequestMapping("peers")
@Slf4j
@Tag(name = "6. Peer Management", description = "Endpoints to manage peers and search for users")
public class PeerManagementController {
    private final PeersManagementService peersManagementService;

    /**
     * Constructor for PeerManagementController.
     *
     * @param peersManagementService the peers management service
     */
    public PeerManagementController(
            PeersManagementService peersManagementService) {
        this.peersManagementService = peersManagementService;
    }

    /**
     * Searches for users based on the provided entry.
     *
     * @param entry the search entry
     * @return the response entity containing the search results or an error status
     */
    @Operation(summary = "Search users", tags = {"6. Peer Management"})
    @GetMapping("search-user/{entry}")
    public ResponseEntity<ApiResponse<List<SearchResultDTO>>> searchUsers(@PathVariable String entry) {
        return peersManagementService.searchUsers(entry);
    }

    /**
     * Sends a peer request to the specified user.
     *
     * @param userId the ID of the user to send the peer request to
     * @return the response entity containing the status of the request or an error status
     */
    @Operation(summary = "Send peer request", tags = {"6. Peer Management"})
    @PostMapping("send-peer-request/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> addPeer(@PathVariable UUID userId) {
        return peersManagementService.addPeer(userId);
    }

    /**
     * Retrieves peer requests based on the specified type.
     *
     * @param type the type of the peer requests to retrieve
     * @return the response entity containing the list of peer requests or an error status
     */
    @Operation(summary = "Get peer requests", tags = {"6. Peer Management"})
    @GetMapping("get-peer-requests/{type}")
    public ResponseEntity<ApiResponse<List<PeerInfoDTO>>> getRequests(@PathVariable ReqType type) {
        return peersManagementService.getRequests(type);
    }

    /**
     * Accepts a peer request with the specified request ID.
     *
     * @param reqId the ID of the peer request to accept
     * @return the response entity containing the status of the acceptance or an error status
     */
    @Operation(summary = "Accept peer request", tags = {"6. Peer Management"})
    @PutMapping("accept-peer-request/{reqId}")
    public ResponseEntity<ApiResponse<Boolean>> acceptRequest(@PathVariable UUID reqId) {
        return peersManagementService.acceptRequest(reqId);
    }

    /**
     * Retrieves the list of peers for the authenticated user.
     *
     * @return the response entity containing the list of peers or an error status
     */
    @Operation(summary = "Get peers", tags = {"6. Peer Management"})
    @GetMapping("get-peers")
    public ResponseEntity<ApiResponse<List<PeerInfoDTO>>> getPeers() {
        return peersManagementService.findPeers();
    }

    /**
     * Removes a peer with the specified peer ID.
     *
     * @param peerId the ID of the peer to remove
     * @return the response entity containing the status of the removal or an error status
     */
    @Operation(summary = "Remove peer", tags = {"6. Peer Management"})
    @DeleteMapping("remove-peer/{peerId}")
    public ResponseEntity<ApiResponse<Boolean>> removePeer(@PathVariable UUID peerId) {
        return peersManagementService.removePeer(peerId);
    }
}
