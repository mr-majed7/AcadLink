package com.majed.acadlink.api.v1.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.peers.PeerInfoDTO;
import com.majed.acadlink.dto.peers.SearchResultDTO;
import com.majed.acadlink.enums.PeerStatus;
import com.majed.acadlink.enums.ReqType;
import com.majed.acadlink.service.PeersManagementService;

@ExtendWith(MockitoExtension.class)
class PeerManagementControllerTest {

    @Mock
    private PeersManagementService peersManagementService;

    @InjectMocks
    private PeerManagementController peerManagementController;

    private UUID testUserId;
    private UUID testPeerId;
    private SearchResultDTO sampleUser;
    private PeerInfoDTO samplePeer;
    private List<SearchResultDTO> sampleUsers;
    private List<PeerInfoDTO> samplePeers;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testPeerId = UUID.randomUUID();

        // Setup sample user for search results
        sampleUser = new SearchResultDTO(
            testUserId,
            "John",
            "Doe",
            "john.doe@example.com",
            "johndoe",
            "Test University",
            PeerStatus.FALSE
        );

        // Setup sample peer
        samplePeer = new PeerInfoDTO(
            UUID.randomUUID(),
            testPeerId,
            "Jane",
            "Smith",
            "jane.smith@example.com",
            "Test University",
            "janesmith"
        );

        sampleUsers = Arrays.asList(sampleUser);
        samplePeers = Arrays.asList(samplePeer);
    }

    @Test
    void searchUsers_Success() {
        // Arrange
        String searchEntry = "john";
        ResponseEntity<ApiResponse<List<SearchResultDTO>>> expectedResponse = 
            ApiResponse.success(sampleUsers, HttpStatus.OK);
        when(peersManagementService.searchUsers(anyString())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<SearchResultDTO>>> response = 
            peerManagementController.searchUsers(searchEntry);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleUsers, response.getBody().getData());
        verify(peersManagementService, times(1)).searchUsers(searchEntry);
    }

    @Test
    void searchUsers_UserNotAuthenticated() {
        // Arrange
        String searchEntry = "john";
        ResponseEntity<ApiResponse<List<SearchResultDTO>>> expectedResponse = 
            ApiResponse.error("User not found", HttpStatus.BAD_REQUEST);
        when(peersManagementService.searchUsers(anyString())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<SearchResultDTO>>> response = 
            peerManagementController.searchUsers(searchEntry);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getError());
        verify(peersManagementService, times(1)).searchUsers(searchEntry);
    }

    @Test
    void addPeer_Success() {
        // Arrange
        ResponseEntity<ApiResponse<Boolean>> expectedResponse = 
            ApiResponse.success(true, HttpStatus.CREATED);
        when(peersManagementService.addPeer(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            peerManagementController.addPeer(testPeerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().getData());
        verify(peersManagementService, times(1)).addPeer(testPeerId);
    }

    @Test
    void addPeer_AlreadyPeers() {
        // Arrange
        ResponseEntity<ApiResponse<Boolean>> expectedResponse = 
            ApiResponse.error("Already a peer", HttpStatus.BAD_REQUEST);
        when(peersManagementService.addPeer(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            peerManagementController.addPeer(testPeerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Already a peer", response.getBody().getError());
        verify(peersManagementService, times(1)).addPeer(testPeerId);
    }

    @Test
    void addPeer_RequestAlreadySent() {
        // Arrange
        ResponseEntity<ApiResponse<Boolean>> expectedResponse = 
            ApiResponse.error("Request already sent", HttpStatus.BAD_REQUEST);
        when(peersManagementService.addPeer(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            peerManagementController.addPeer(testPeerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request already sent", response.getBody().getError());
        verify(peersManagementService, times(1)).addPeer(testPeerId);
    }

    @Test
    void getRequests_Sent_Success() {
        // Arrange
        ResponseEntity<ApiResponse<List<PeerInfoDTO>>> expectedResponse = 
            ApiResponse.success(samplePeers, HttpStatus.OK);
        when(peersManagementService.getRequests(ReqType.SENT)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<PeerInfoDTO>>> response = 
            peerManagementController.getRequests(ReqType.SENT);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(samplePeers, response.getBody().getData());
        verify(peersManagementService, times(1)).getRequests(ReqType.SENT);
    }

    @Test
    void getRequests_Received_Success() {
        // Arrange
        ResponseEntity<ApiResponse<List<PeerInfoDTO>>> expectedResponse = 
            ApiResponse.success(samplePeers, HttpStatus.OK);
        when(peersManagementService.getRequests(ReqType.RECEIVED)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<PeerInfoDTO>>> response = 
            peerManagementController.getRequests(ReqType.RECEIVED);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(samplePeers, response.getBody().getData());
        verify(peersManagementService, times(1)).getRequests(ReqType.RECEIVED);
    }

    @Test
    void acceptRequest_Success() {
        // Arrange
        UUID requestId = UUID.randomUUID();
        ResponseEntity<ApiResponse<Boolean>> expectedResponse = 
            ApiResponse.success(true, HttpStatus.OK);
        when(peersManagementService.acceptRequest(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            peerManagementController.acceptRequest(requestId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().getData());
        verify(peersManagementService, times(1)).acceptRequest(requestId);
    }

    @Test
    void acceptRequest_NotAuthorized() {
        // Arrange
        UUID requestId = UUID.randomUUID();
        ResponseEntity<ApiResponse<Boolean>> expectedResponse = 
            ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
        when(peersManagementService.acceptRequest(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            peerManagementController.acceptRequest(requestId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(peersManagementService, times(1)).acceptRequest(requestId);
    }

    @Test
    void getPeers_Success() {
        // Arrange
        ResponseEntity<ApiResponse<List<PeerInfoDTO>>> expectedResponse = 
            ApiResponse.success(samplePeers, HttpStatus.OK);
        when(peersManagementService.findPeers()).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<PeerInfoDTO>>> response = 
            peerManagementController.getPeers();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(samplePeers, response.getBody().getData());
        verify(peersManagementService, times(1)).findPeers();
    }

    @Test
    void getPeers_UserNotAuthenticated() {
        // Arrange
        ResponseEntity<ApiResponse<List<PeerInfoDTO>>> expectedResponse = 
            ApiResponse.error("User not found", HttpStatus.BAD_REQUEST);
        when(peersManagementService.findPeers()).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<PeerInfoDTO>>> response = 
            peerManagementController.getPeers();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getError());
        verify(peersManagementService, times(1)).findPeers();
    }

    @Test
    void removePeer_Success() {
        // Arrange
        UUID peerId = UUID.randomUUID();
        ResponseEntity<ApiResponse<Boolean>> expectedResponse = 
            ApiResponse.success(true, HttpStatus.OK);
        when(peersManagementService.removePeer(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            peerManagementController.removePeer(peerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().getData());
        verify(peersManagementService, times(1)).removePeer(peerId);
    }

    @Test
    void removePeer_NotAuthorized() {
        // Arrange
        UUID peerId = UUID.randomUUID();
        ResponseEntity<ApiResponse<Boolean>> expectedResponse = 
            ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
        when(peersManagementService.removePeer(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            peerManagementController.removePeer(peerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(peersManagementService, times(1)).removePeer(peerId);
    }

    @Test
    void removePeer_PeerNotFound() {
        // Arrange
        UUID peerId = UUID.randomUUID();
        ResponseEntity<ApiResponse<Boolean>> expectedResponse = 
            ApiResponse.error("Peer not found", HttpStatus.NOT_FOUND);
        when(peersManagementService.removePeer(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            peerManagementController.removePeer(peerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Peer not found", response.getBody().getError());
        verify(peersManagementService, times(1)).removePeer(peerId);
    }
} 