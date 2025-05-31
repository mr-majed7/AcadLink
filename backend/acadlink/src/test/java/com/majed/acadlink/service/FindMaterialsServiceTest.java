package com.majed.acadlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.majed.acadlink.domain.entitie.Folder;
import com.majed.acadlink.domain.entitie.Materials;
import com.majed.acadlink.domain.entitie.Peers;
import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.MaterialsRepo;
import com.majed.acadlink.domain.repository.PeersRepo;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.enums.PeerStatus;
import com.majed.acadlink.enums.Privacy;
import com.majed.acadlink.utility.GetUserUtil;

@ExtendWith(MockitoExtension.class)
class FindMaterialsServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private MaterialsRepo materialsRepo;

    @Mock
    private PeersRepo peersRepo;

    @Mock
    private GetUserUtil getUserUtil;

    @InjectMocks
    private FindMaterialsService findMaterialsService;

    private UUID testUserId;
    private UUID testPeerId;
    private UUID testFolderId;
    private User testUser;
    private User testPeer;
    private Materials publicMaterial;
    private Materials peerMaterial;
    private Materials institutionalMaterial;
    private Peers peerConnection;
    private Folder testFolder;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testPeerId = UUID.randomUUID();
        testFolderId = UUID.randomUUID();

        // Setup test folder
        testFolder = new Folder();
        testFolder.setId(testFolderId);
        testFolder.setName("Test Folder");
        testFolder.setUser(testUser);

        // Setup test user
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setInstitute("Test Institute");

        // Setup test peer
        testPeer = new User();
        testPeer.setId(testPeerId);
        testPeer.setInstitute("Test Institute");

        // Setup peer connection
        peerConnection = new Peers();
        peerConnection.setUser1(testUser);
        peerConnection.setUser2(testPeer);
        peerConnection.setStatus(PeerStatus.ACCEPTED);

        // Setup test materials
        publicMaterial = new Materials();
        publicMaterial.setId(UUID.randomUUID());
        publicMaterial.setName("Public Material");
        publicMaterial.setLink("http://example.com/public");
        publicMaterial.setType(MaterialType.BOOK);
        publicMaterial.setPrivacy(Privacy.PUBLIC);
        publicMaterial.setFolder(testFolder);

        peerMaterial = new Materials();
        peerMaterial.setId(UUID.randomUUID());
        peerMaterial.setName("Peer Material");
        peerMaterial.setLink("http://example.com/peer");
        peerMaterial.setType(MaterialType.LECTURE_NOTE);
        peerMaterial.setPrivacy(Privacy.PEERS);
        peerMaterial.setFolder(testFolder);

        institutionalMaterial = new Materials();
        institutionalMaterial.setId(UUID.randomUUID());
        institutionalMaterial.setName("Institutional Material");
        institutionalMaterial.setLink("http://example.com/institutional");
        institutionalMaterial.setType(MaterialType.OTHER);
        institutionalMaterial.setPrivacy(Privacy.INSTITUTIONAL);
        institutionalMaterial.setFolder(testFolder);
    }

    @Test
    void searchMaterials_Success() {
        // Arrange
        String keywords = "material";
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(materialsRepo.searchPublicMaterials(keywords, Privacy.PUBLIC))
            .thenReturn(List.of(publicMaterial));
        when(materialsRepo.searchPeerMaterials(keywords, testUserId, Privacy.PEERS))
            .thenReturn(List.of(peerMaterial));
        when(materialsRepo.searchInstitutionalMaterials(keywords, testUser.getInstitute(), Privacy.INSTITUTIONAL))
            .thenReturn(List.of(institutionalMaterial));

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialsService.searchMaterials(keywords);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getData().size());
        verify(materialsRepo, times(1)).searchPublicMaterials(keywords, Privacy.PUBLIC);
        verify(materialsRepo, times(1)).searchPeerMaterials(keywords, testUserId, Privacy.PEERS);
        verify(materialsRepo, times(1)).searchInstitutionalMaterials(keywords, testUser.getInstitute(), Privacy.INSTITUTIONAL);
    }

    @Test
    void searchMaterials_UserNotAuthenticated() {
        // Arrange
        String keywords = "material";
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialsService.searchMaterials(keywords);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getError());
        verify(materialsRepo, times(0)).searchPublicMaterials(anyString(), any(Privacy.class));
        verify(materialsRepo, times(0)).searchPeerMaterials(anyString(), any(UUID.class), any(Privacy.class));
        verify(materialsRepo, times(0)).searchInstitutionalMaterials(anyString(), anyString(), any(Privacy.class));
    }

    @Test
    void searchMaterials_NoResults() {
        // Arrange
        String keywords = "nonexistent";
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(materialsRepo.searchPublicMaterials(keywords, Privacy.PUBLIC)).thenReturn(List.of());
        when(materialsRepo.searchPeerMaterials(keywords, testUserId, Privacy.PEERS)).thenReturn(List.of());
        when(materialsRepo.searchInstitutionalMaterials(keywords, testUser.getInstitute(), Privacy.INSTITUTIONAL))
            .thenReturn(List.of());

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialsService.searchMaterials(keywords);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getData().size());
        verify(materialsRepo, times(1)).searchPublicMaterials(keywords, Privacy.PUBLIC);
        verify(materialsRepo, times(1)).searchPeerMaterials(keywords, testUserId, Privacy.PEERS);
        verify(materialsRepo, times(1)).searchInstitutionalMaterials(keywords, testUser.getInstitute(), Privacy.INSTITUTIONAL);
    }

    @Test
    void findPeerMaterials_Success() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(userRepo.findById(testPeerId)).thenReturn(Optional.of(testPeer));
        when(peersRepo.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(testUserId, testPeerId, testUserId, testPeerId))
            .thenReturn(peerConnection);
        when(materialsRepo.findAllByUserIdAndPrivacy(testPeerId, testUser.getInstitute()))
            .thenReturn(Arrays.asList(publicMaterial, peerMaterial, institutionalMaterial));

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialsService.findPeerMaterials(testPeerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getData().size());
        verify(userRepo, times(1)).findById(testPeerId);
        verify(peersRepo, times(1)).findByUser1IdAndUser2IdOrUser2IdAndUser1Id(testUserId, testPeerId, testUserId, testPeerId);
        verify(materialsRepo, times(1)).findAllByUserIdAndPrivacy(testPeerId, testUser.getInstitute());
    }

    @Test
    void findPeerMaterials_UserNotAuthenticated() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialsService.findPeerMaterials(testPeerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not logged in", response.getBody().getError());
        verify(userRepo, times(0)).findById(any(UUID.class));
        verify(peersRepo, times(0)).findByUser1IdAndUser2IdOrUser2IdAndUser1Id(any(UUID.class), any(UUID.class), any(UUID.class), any(UUID.class));
        verify(materialsRepo, times(0)).findAllByUserIdAndPrivacy(any(UUID.class), anyString());
    }

    @Test
    void findPeerMaterials_PeerNotFound() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(userRepo.findById(testPeerId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialsService.findPeerMaterials(testPeerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No user exists with this id", response.getBody().getError());
        verify(userRepo, times(1)).findById(testPeerId);
        verify(peersRepo, times(0)).findByUser1IdAndUser2IdOrUser2IdAndUser1Id(any(UUID.class), any(UUID.class), any(UUID.class), any(UUID.class));
        verify(materialsRepo, times(0)).findAllByUserIdAndPrivacy(any(UUID.class), anyString());
    }

    @Test
    void findPeerMaterials_NotPeers() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(userRepo.findById(testPeerId)).thenReturn(Optional.of(testPeer));
        when(peersRepo.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(testUserId, testPeerId, testUserId, testPeerId))
            .thenReturn(null);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialsService.findPeerMaterials(testPeerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Users are not peers", response.getBody().getError());
        verify(userRepo, times(1)).findById(testPeerId);
        verify(peersRepo, times(1)).findByUser1IdAndUser2IdOrUser2IdAndUser1Id(testUserId, testPeerId, testUserId, testPeerId);
        verify(materialsRepo, times(0)).findAllByUserIdAndPrivacy(any(UUID.class), anyString());
    }

    @Test
    void findPeerMaterials_PendingPeerRequest() {
        // Arrange
        peerConnection.setStatus(PeerStatus.PENDING);
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(userRepo.findById(testPeerId)).thenReturn(Optional.of(testPeer));
        when(peersRepo.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(testUserId, testPeerId, testUserId, testPeerId))
            .thenReturn(peerConnection);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialsService.findPeerMaterials(testPeerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Users are not peers", response.getBody().getError());
        verify(userRepo, times(1)).findById(testPeerId);
        verify(peersRepo, times(1)).findByUser1IdAndUser2IdOrUser2IdAndUser1Id(testUserId, testPeerId, testUserId, testPeerId);
        verify(materialsRepo, times(0)).findAllByUserIdAndPrivacy(any(UUID.class), anyString());
    }
} 