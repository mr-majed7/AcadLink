package com.majed.acadlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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

import com.majed.acadlink.domain.entity.Folder;
import com.majed.acadlink.domain.entity.Materials;
import com.majed.acadlink.domain.entity.User;
import com.majed.acadlink.domain.repository.FolderRepo;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.folder.AllFolderResponseDTO;
import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderResponseDTO;
import com.majed.acadlink.dto.folder.UpdateFolderResponseDTO;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.enums.Privacy;
import com.majed.acadlink.utility.AuthorizationCheck;
import com.majed.acadlink.utility.GetUserUtil;

@ExtendWith(MockitoExtension.class)
class FolderServiceTest {

    @Mock
    private FolderRepo folderRepo;

    @Mock
    private GetUserUtil getUserUtil;

    @Mock
    private AuthorizationCheck authorizationCheck;

    @InjectMocks
    private FolderService folderService;

    private UUID testUserId;
    private UUID testFolderId;
    private User testUser;
    private Folder testFolder;
    private FolderCreateDTO folderCreateDTO;
    private Materials testMaterial;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testFolderId = UUID.randomUUID();

        // Setup test user
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setInstitute("Test Institute");

        // Setup test folder
        testFolder = new Folder();
        testFolder.setId(testFolderId);
        testFolder.setName("Test Folder");
        testFolder.setCreatedAt(LocalDate.now());
        testFolder.setPrivacy(Privacy.PUBLIC);
        testFolder.setUser(testUser);

        // Setup test material
        testMaterial = new Materials();
        testMaterial.setId(UUID.randomUUID());
        testMaterial.setName("Test Material");
        testMaterial.setLink("http://example.com/test.pdf");
        testMaterial.setType(MaterialType.BOOK);
        testMaterial.setPrivacy(Privacy.PUBLIC);
        testMaterial.setFolder(testFolder);

        testFolder.setMaterials(Arrays.asList(testMaterial));

        // Setup folder create DTO
        folderCreateDTO = new FolderCreateDTO();
        folderCreateDTO.setName("Test Folder");
        folderCreateDTO.setPrivacy(Privacy.PUBLIC);
    }

    @Test
    void addFolder_Success() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(folderRepo.save(any(Folder.class))).thenReturn(testFolder);

        // Act
        ResponseEntity<ApiResponse<AllFolderResponseDTO>> response = folderService.addFolder(folderCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(testFolderId, response.getBody().getData().getId());
        assertEquals("Test Folder", response.getBody().getData().getName());
        assertEquals(Privacy.PUBLIC, response.getBody().getData().getPrivacy());
        verify(folderRepo, times(1)).save(any(Folder.class));
    }

    @Test
    void addFolder_UserNotAuthenticated() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<AllFolderResponseDTO>> response = folderService.addFolder(folderCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No user found", response.getBody().getError());
        verify(folderRepo, times(0)).save(any(Folder.class));
    }

    @Test
    void getAllFolders_Success() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(folderRepo.findByUserId(testUserId)).thenReturn(Arrays.asList(testFolder));

        // Act
        ResponseEntity<ApiResponse<List<AllFolderResponseDTO>>> response = folderService.getAllFolders();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(testFolderId, response.getBody().getData().get(0).getId());
        assertEquals("Test Folder", response.getBody().getData().get(0).getName());
        assertEquals(Privacy.PUBLIC, response.getBody().getData().get(0).getPrivacy());
        verify(folderRepo, times(1)).findByUserId(testUserId);
    }

    @Test
    void getAllFolders_UserNotAuthenticated() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<List<AllFolderResponseDTO>>> response = folderService.getAllFolders();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No user found", response.getBody().getError());
        verify(folderRepo, times(0)).findByUserId(any(UUID.class));
    }

    @Test
    void getFolder_Success() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(true);
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));

        // Act
        ResponseEntity<ApiResponse<FolderResponseDTO>> response = folderService.getFolder(testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(testFolderId, response.getBody().getData().getId());
        assertEquals("Test Folder", response.getBody().getData().getName());
        assertEquals(Privacy.PUBLIC, response.getBody().getData().getPrivacy());
        assertEquals(1, response.getBody().getData().getMaterials().size());
        verify(folderRepo, times(1)).findById(testFolderId);
    }

    @Test
    void getFolder_UserNotAuthenticated() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<FolderResponseDTO>> response = folderService.getFolder(testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No user found", response.getBody().getError());
        verify(folderRepo, times(0)).findById(any(UUID.class));
    }

    @Test
    void getFolder_NotAuthorized() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse<FolderResponseDTO>> response = folderService.getFolder(testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not Authorized", response.getBody().getError());
        verify(folderRepo, times(0)).findById(any(UUID.class));
    }

    @Test
    void getFolder_NotFound() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(true);
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<FolderResponseDTO>> response = folderService.getFolder(testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Folder not found", response.getBody().getError());
        verify(folderRepo, times(1)).findById(testFolderId);
    }

    @Test
    void updateFolder_Success() {
        // Arrange
        FolderCreateDTO updateDTO = new FolderCreateDTO();
        updateDTO.setName("Updated Folder");
        updateDTO.setPrivacy(Privacy.PEERS);

        Folder updatedFolder = new Folder();
        updatedFolder.setId(testFolderId);
        updatedFolder.setName("Updated Folder");
        updatedFolder.setCreatedAt(LocalDate.now());
        updatedFolder.setPrivacy(Privacy.PEERS);
        updatedFolder.setUser(testUser);

        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(true);
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(folderRepo.save(any(Folder.class))).thenReturn(updatedFolder);

        // Act
        ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> response = folderService.updateFolder(testFolderId, updateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(testFolderId, response.getBody().getData().getId());
        assertEquals("Updated Folder", response.getBody().getData().getName());
        assertEquals(Privacy.PEERS, response.getBody().getData().getPrivacy());
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(folderRepo, times(1)).save(any(Folder.class));
    }

    @Test
    void updateFolder_UserNotAuthenticated() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> response = folderService.updateFolder(testFolderId, folderCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No user found", response.getBody().getError());
        verify(folderRepo, times(0)).findById(any(UUID.class));
        verify(folderRepo, times(0)).save(any(Folder.class));
    }

    @Test
    void updateFolder_NotAuthorized() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> response = folderService.updateFolder(testFolderId, folderCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(folderRepo, times(0)).findById(any(UUID.class));
        verify(folderRepo, times(0)).save(any(Folder.class));
    }

    @Test
    void updateFolder_NotFound() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(true);
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> response = folderService.updateFolder(testFolderId, folderCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No folder found", response.getBody().getError());
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(folderRepo, times(0)).save(any(Folder.class));
    }
} 