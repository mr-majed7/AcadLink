package com.majed.acadlink.api.v1.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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

import com.majed.acadlink.domain.entitie.Folder;
import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.folder.AllFolderResponseDTO;
import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderResponseDTO;
import com.majed.acadlink.dto.folder.UpdateFolderResponseDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.enums.Privacy;
import com.majed.acadlink.service.FolderService;

@ExtendWith(MockitoExtension.class)
class FolderControllerTest {

    @Mock
    private FolderService folderService;

    @InjectMocks
    private FolderController folderController;

    private UUID testFolderId;
    private User testUser;
    private Folder testFolder;
    private FolderCreateDTO folderCreateDTO;
    private AllFolderResponseDTO sampleFolder;
    private List<AllFolderResponseDTO> sampleFolders;
    private FolderResponseDTO folderWithMaterials;
    private UpdateFolderResponseDTO updatedFolder;

    @BeforeEach
    void setUp() {
        testFolderId = UUID.randomUUID();

        // Setup folder create DTO
        folderCreateDTO = new FolderCreateDTO();
        folderCreateDTO.setName("Test Folder");
        folderCreateDTO.setPrivacy(Privacy.PUBLIC);

        // Setup sample folder
        sampleFolder = new AllFolderResponseDTO(
            testFolderId,
            "Test Folder",
            LocalDate.now(),
            Privacy.PUBLIC
        );

        // Setup sample folders list
        sampleFolders = Arrays.asList(sampleFolder);

        // Setup sample materials
        MaterialResponseDTO sampleMaterial = new MaterialResponseDTO(
            UUID.randomUUID(),
            "Test Material",
            "http://example.com/test.pdf",
            MaterialType.BOOK,
            Privacy.PUBLIC,
            testFolderId
        );

        // Setup folder with materials
        folderWithMaterials = new FolderResponseDTO(
            testFolderId,
            "Test Folder",
            LocalDate.now(),
            Privacy.PUBLIC,
            Arrays.asList(sampleMaterial)
        );

        // Setup updated folder
        updatedFolder = new UpdateFolderResponseDTO(
            testFolderId,
            "Updated Folder",
            LocalDate.now(),
            Privacy.PEERS
        );
    }

    @Test
    void createFolder_Success() {
        // Arrange
        ResponseEntity<ApiResponse<AllFolderResponseDTO>> expectedResponse = 
            ApiResponse.success(sampleFolder, HttpStatus.CREATED);
        when(folderService.addFolder(any(FolderCreateDTO.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<AllFolderResponseDTO>> response = 
            folderController.createFolder(folderCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleFolder, response.getBody().getData());
        verify(folderService, times(1)).addFolder(folderCreateDTO);
    }

    @Test
    void createFolder_UserNotAuthenticated() {
        // Arrange
        ResponseEntity<ApiResponse<AllFolderResponseDTO>> expectedResponse = 
            ApiResponse.error("No user found", HttpStatus.BAD_REQUEST);
        when(folderService.addFolder(any(FolderCreateDTO.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<AllFolderResponseDTO>> response = 
            folderController.createFolder(folderCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No user found", response.getBody().getError());
        verify(folderService, times(1)).addFolder(folderCreateDTO);
    }

    @Test
    void getAllFolders_Success() {
        // Arrange
        ResponseEntity<ApiResponse<List<AllFolderResponseDTO>>> expectedResponse = 
            ApiResponse.success(sampleFolders, HttpStatus.OK);
        when(folderService.getAllFolders()).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<AllFolderResponseDTO>>> response = 
            folderController.getAllFolders();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleFolders, response.getBody().getData());
        verify(folderService, times(1)).getAllFolders();
    }

    @Test
    void getAllFolders_UserNotAuthenticated() {
        // Arrange
        ResponseEntity<ApiResponse<List<AllFolderResponseDTO>>> expectedResponse = 
            ApiResponse.error("No user found", HttpStatus.BAD_REQUEST);
        when(folderService.getAllFolders()).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<AllFolderResponseDTO>>> response = 
            folderController.getAllFolders();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No user found", response.getBody().getError());
        verify(folderService, times(1)).getAllFolders();
    }

    @Test
    void getFolder_Success() {
        // Arrange
        ResponseEntity<ApiResponse<FolderResponseDTO>> expectedResponse = 
            ApiResponse.success(folderWithMaterials, HttpStatus.OK);
        when(folderService.getFolder(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<FolderResponseDTO>> response = 
            folderController.getFolder(testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(folderWithMaterials, response.getBody().getData());
        verify(folderService, times(1)).getFolder(testFolderId);
    }

    @Test
    void getFolder_UserNotAuthenticated() {
        // Arrange
        ResponseEntity<ApiResponse<FolderResponseDTO>> expectedResponse = 
            ApiResponse.error("No user found", HttpStatus.BAD_REQUEST);
        when(folderService.getFolder(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<FolderResponseDTO>> response = 
            folderController.getFolder(testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No user found", response.getBody().getError());
        verify(folderService, times(1)).getFolder(testFolderId);
    }

    @Test
    void getFolder_NotAuthorized() {
        // Arrange
        ResponseEntity<ApiResponse<FolderResponseDTO>> expectedResponse = 
            ApiResponse.error("Not Authorized", HttpStatus.FORBIDDEN);
        when(folderService.getFolder(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<FolderResponseDTO>> response = 
            folderController.getFolder(testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not Authorized", response.getBody().getError());
        verify(folderService, times(1)).getFolder(testFolderId);
    }

    @Test
    void getFolder_NotFound() {
        // Arrange
        ResponseEntity<ApiResponse<FolderResponseDTO>> expectedResponse = 
            ApiResponse.error("Folder not found", HttpStatus.NOT_FOUND);
        when(folderService.getFolder(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<FolderResponseDTO>> response = 
            folderController.getFolder(testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Folder not found", response.getBody().getError());
        verify(folderService, times(1)).getFolder(testFolderId);
    }

    @Test
    void updateFolder_Success() {
        // Arrange
        ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> expectedResponse = 
            ApiResponse.success(updatedFolder, HttpStatus.OK);
        when(folderService.updateFolder(any(UUID.class), any(FolderCreateDTO.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> response = 
            folderController.updateFolder(testFolderId, folderCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedFolder, response.getBody().getData());
        verify(folderService, times(1)).updateFolder(testFolderId, folderCreateDTO);
    }

    @Test
    void updateFolder_UserNotAuthenticated() {
        // Arrange
        ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> expectedResponse = 
            ApiResponse.error("No user found", HttpStatus.BAD_REQUEST);
        when(folderService.updateFolder(any(UUID.class), any(FolderCreateDTO.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> response = 
            folderController.updateFolder(testFolderId, folderCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No user found", response.getBody().getError());
        verify(folderService, times(1)).updateFolder(testFolderId, folderCreateDTO);
    }

    @Test
    void updateFolder_NotAuthorized() {
        // Arrange
        ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> expectedResponse = 
            ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
        when(folderService.updateFolder(any(UUID.class), any(FolderCreateDTO.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> response = 
            folderController.updateFolder(testFolderId, folderCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(folderService, times(1)).updateFolder(testFolderId, folderCreateDTO);
    }

    @Test
    void updateFolder_NotFound() {
        // Arrange
        ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> expectedResponse = 
            ApiResponse.error("No folder found", HttpStatus.BAD_REQUEST);
        when(folderService.updateFolder(any(UUID.class), any(FolderCreateDTO.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> response = 
            folderController.updateFolder(testFolderId, folderCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No folder found", response.getBody().getError());
        verify(folderService, times(1)).updateFolder(testFolderId, folderCreateDTO);
    }
} 