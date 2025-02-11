package com.majed.acadlink.api.v1.controller;

import com.majed.acadlink.dto.peers.SearchResultDTO;
import com.majed.acadlink.service.PeersManagementService;
import com.majed.acadlink.utility.AuthorizationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("peers")
@Slf4j
public class PeerManagementController {
    @Autowired
    private AuthorizationCheck authorizationCheck;

    @Autowired
    private PeersManagementService peersManagementService;

    @GetMapping("search-user/{entry}")
    public ResponseEntity<List<SearchResultDTO>> searchUsers(@PathVariable String entry) {
        List<SearchResultDTO> users = peersManagementService.searchUsers(entry);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
