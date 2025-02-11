package com.majed.acadlink.api.v1.controller;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.dto.peers.SearchResultDTO;
import com.majed.acadlink.service.PeersManagementService;
import com.majed.acadlink.utility.AuthorizationCheck;
import com.majed.acadlink.utility.GetUserUtil;
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
public class PeerManagementController {
    @Autowired
    private AuthorizationCheck authorizationCheck;

    @Autowired
    private PeersManagementService peersManagementService;

    @Autowired
    private GetUserUtil getUserUtil;

    @GetMapping("search-user/{entry}")
    public ResponseEntity<List<SearchResultDTO>> searchUsers(@PathVariable String entry) {
        Optional<User> currentUser = getUserUtil.getAuthenticatedUser();
        List<SearchResultDTO> users = peersManagementService.searchUsers(entry, currentUser.get().getId());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("send-peer-request/{userId}")
    public ResponseEntity<Boolean> addPeer(@PathVariable UUID userId) {
        Optional<User> currentUser = getUserUtil.getAuthenticatedUser();
        if (peersManagementService.addPeer(currentUser.get().getId(), userId)) {
            return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
