package com.majed.acadlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

@ExtendWith(MockitoExtension.class)
class PeersManagementServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PeersRepo peersRepo;

    @Mock
    private GetUserUtil getUserUtil;

    @Mock
    private AuthorizationCheck authorizationCheck;

    @InjectMocks
    private PeersManagementService peersManagementService;

    private UUID testUserId;
    private UUID testPeerId;
    private User testUser;
    private User peerUser;
    private Peers testPeer;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testPeerId = UUID.randomUUID();

        // Setup test user
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setUsername("johndoe");
        testUser.setInstitute("Test University");

        // Setup peer user
        peerUser = new User();
        peerUser.setId(testPeerId);
        peerUser.setFirstName("Jane");
        peerUser.setLastName("Smith");
        peerUser.setEmail("jane.smith@example.com");
        peerUser.setUsername("janesmith");
        peerUser.setInstitute("Test University");

        // Setup test peer relationship
        testPeer = new Peers();
        testPeer.setId(UUID.randomUUID());
        testPeer.setUser1(testUser);
        testPeer.setUser2(peerUser);
        testPeer.setStatus(PeerStatus.PENDING);
    }

    @Test
    void searchUsers_Success() {
        // Arrange
        String searchEntry = "jane";
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(userRepo.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchEntry, searchEntry))
                .thenReturn(Arrays.asList(peerUser));
        when(peersRepo.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(testUserId, testPeerId, testUserId, testPeerId))
                .thenReturn(null);

        // Act
        ResponseEntity<ApiResponse<List<SearchResultDTO>>> response =
                peersManagementService.searchUsers(searchEntry);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(testPeerId, response.getBody().getData().get(0).getId());
        verify(userRepo, times(1)).findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchEntry, searchEntry);
    }

    @Test
    void searchUsers_UserNotAuthenticated() {
        // Arrange
        String searchEntry = "jane";
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<List<SearchResultDTO>>> response =
                peersManagementService.searchUsers(searchEntry);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getError());
        verify(userRepo, times(0)).findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(anyString(), anyString());
    }

    @Test
    void addPeer_Success() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(peersRepo.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(testUserId, testPeerId, testUserId, testPeerId))
                .thenReturn(null);
        when(userRepo.findById(testPeerId)).thenReturn(Optional.of(peerUser));
        when(peersRepo.save(any(Peers.class))).thenReturn(testPeer);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response =
                peersManagementService.addPeer(testPeerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().getData());
        verify(peersRepo, times(1)).save(any(Peers.class));
    }

    @Test
    void addPeer_AlreadyPeers() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        testPeer.setStatus(PeerStatus.ACCEPTED);
        when(peersRepo.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(testUserId, testPeerId, testUserId, testPeerId))
                .thenReturn(testPeer);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response =
                peersManagementService.addPeer(testPeerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Already a peer", response.getBody().getError());
        verify(peersRepo, times(0)).save(any(Peers.class));
    }

    @Test
    void addPeer_RequestAlreadySent() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        testPeer.setStatus(PeerStatus.PENDING);
        when(peersRepo.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(testUserId, testPeerId, testUserId, testPeerId))
                .thenReturn(testPeer);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response =
                peersManagementService.addPeer(testPeerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request already sent", response.getBody().getError());
        verify(peersRepo, times(0)).save(any(Peers.class));
    }

    @Test
    void getRequests_Sent_Success() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(peersRepo.findByUser1IdAndStatus(testUserId, PeerStatus.PENDING))
                .thenReturn(Arrays.asList(testPeer));
        when(userRepo.findById(testPeerId)).thenReturn(Optional.of(peerUser));

        // Act
        ResponseEntity<ApiResponse<List<PeerInfoDTO>>> response =
                peersManagementService.getRequests(ReqType.SENT);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(testPeerId, response.getBody().getData().get(0).getUserId());
        verify(peersRepo, times(1)).findByUser1IdAndStatus(testUserId, PeerStatus.PENDING);
    }

    @Test
    void getRequests_Received_Success() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(peersRepo.findByUser2IdAndStatus(testUserId, PeerStatus.PENDING))
                .thenReturn(Arrays.asList(testPeer));
        // Use lenient stubbing to handle any UUID argument
        lenient().when(userRepo.findById(any(UUID.class))).thenReturn(Optional.of(peerUser));

        // Act
        ResponseEntity<ApiResponse<List<PeerInfoDTO>>> response =
                peersManagementService.getRequests(ReqType.RECEIVED);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(testPeerId, response.getBody().getData().get(0).getUserId());
        verify(peersRepo, times(1)).findByUser2IdAndStatus(testUserId, PeerStatus.PENDING);
    }

    @Test
    void findPeers_Success() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        testPeer.setStatus(PeerStatus.ACCEPTED);
        when(peersRepo.findByUser1IdOrUser2IdAndStatus(testUserId, testUserId, PeerStatus.ACCEPTED))
                .thenReturn(Arrays.asList(testPeer));

        // Act
        ResponseEntity<ApiResponse<List<PeerInfoDTO>>> response =
                peersManagementService.findPeers();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(testPeerId, response.getBody().getData().get(0).getUserId());
        verify(peersRepo, times(1)).findByUser1IdOrUser2IdAndStatus(testUserId, testUserId, PeerStatus.ACCEPTED);
    }

    @Test
    void acceptRequest_Success() {
        // Arrange
        when(peersRepo.findById(testPeer.getId())).thenReturn(Optional.of(testPeer));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(true);
        when(peersRepo.save(any(Peers.class))).thenReturn(testPeer);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response =
                peersManagementService.acceptRequest(testPeer.getId());

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().getData());
        assertEquals(PeerStatus.ACCEPTED, testPeer.getStatus());
        verify(peersRepo, times(1)).save(testPeer);
    }

    @Test
    void acceptRequest_NotAuthorized() {
        // Arrange
        when(peersRepo.findById(testPeer.getId())).thenReturn(Optional.of(testPeer));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(false);
        when(authorizationCheck.checkAuthorization(testPeerId)).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response =
                peersManagementService.acceptRequest(testPeer.getId());

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(peersRepo, times(0)).save(any(Peers.class));
    }

    @Test
    void removePeer_Success() {
        // Arrange
        when(peersRepo.findById(testPeer.getId())).thenReturn(Optional.of(testPeer));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(true);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response =
                peersManagementService.removePeer(testPeer.getId());

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().getData());
        verify(peersRepo, times(1)).delete(testPeer);
    }

    @Test
    void removePeer_NotAuthorized() {
        // Arrange
        when(peersRepo.findById(testPeer.getId())).thenReturn(Optional.of(testPeer));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(false);
        when(authorizationCheck.checkAuthorization(testPeerId)).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response =
                peersManagementService.removePeer(testPeer.getId());

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(peersRepo, times(0)).delete(any(Peers.class));
    }

    @Test
    void removePeer_PeerNotFound() {
        // Arrange
        when(peersRepo.findById(testPeer.getId())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<Boolean>> response =
                peersManagementService.removePeer(testPeer.getId());

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Peer not found", response.getBody().getError());
        verify(peersRepo, times(0)).delete(any(Peers.class));
    }
} 