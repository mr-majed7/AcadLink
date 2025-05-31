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
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.enums.Privacy;
import com.majed.acadlink.service.FindMaterialsService;

@ExtendWith(MockitoExtension.class)
class FindMaterialControllerTest {

    @Mock
    private FindMaterialsService findMaterialsService;

    @InjectMocks
    private FindMaterialController findMaterialController;

    private MaterialResponseDTO sampleMaterial1;
    private MaterialResponseDTO sampleMaterial2;
    private List<MaterialResponseDTO> sampleMaterials;
    private UUID testUserId;
    private UUID testFolderId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testFolderId = UUID.randomUUID();

        // Setup sample materials
        sampleMaterial1 = new MaterialResponseDTO(
            UUID.randomUUID(),
            "Sample Material 1",
            "http://example.com/material1",
            MaterialType.OTHER,
            Privacy.PUBLIC,
            testFolderId
        );

        sampleMaterial2 = new MaterialResponseDTO(
            UUID.randomUUID(),
            "Sample Material 2",
            "http://example.com/material2",
            MaterialType.OTHER,
            Privacy.PEERS,
            testFolderId
        );

        sampleMaterials = Arrays.asList(sampleMaterial1, sampleMaterial2);
    }

    @Test
    void searchMaterials_Success() {
        // Arrange
        String keywords = "sample";
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> expectedResponse = 
            ApiResponse.success(sampleMaterials, HttpStatus.OK);
        when(findMaterialsService.searchMaterials(anyString())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialController.searchMaterials(keywords);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleMaterials, response.getBody().getData());
        verify(findMaterialsService, times(1)).searchMaterials(keywords);
    }

    @Test
    void searchMaterials_NoResults() {
        // Arrange
        String keywords = "nonexistent";
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> expectedResponse = 
            ApiResponse.success(List.of(), HttpStatus.OK);
        when(findMaterialsService.searchMaterials(anyString())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialController.searchMaterials(keywords);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(List.of(), response.getBody().getData());
        verify(findMaterialsService, times(1)).searchMaterials(keywords);
    }

    @Test
    void searchMaterials_UserNotAuthenticated() {
        // Arrange
        String keywords = "sample";
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> expectedResponse = 
            ApiResponse.error("User not found", HttpStatus.BAD_REQUEST);
        when(findMaterialsService.searchMaterials(anyString())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialController.searchMaterials(keywords);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getError());
        verify(findMaterialsService, times(1)).searchMaterials(keywords);
    }

    @Test
    void viewPeersMaterials_Success() {
        // Arrange
        UUID peerUserId = UUID.randomUUID();
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> expectedResponse = 
            ApiResponse.success(sampleMaterials, HttpStatus.OK);
        when(findMaterialsService.findPeerMaterials(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialController.viewPeersMaterials(peerUserId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleMaterials, response.getBody().getData());
        verify(findMaterialsService, times(1)).findPeerMaterials(peerUserId);
    }

    @Test
    void viewPeersMaterials_NotPeers() {
        // Arrange
        UUID peerUserId = UUID.randomUUID();
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> expectedResponse = 
            ApiResponse.error("Users are not peers", HttpStatus.BAD_REQUEST);
        when(findMaterialsService.findPeerMaterials(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialController.viewPeersMaterials(peerUserId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Users are not peers", response.getBody().getError());
        verify(findMaterialsService, times(1)).findPeerMaterials(peerUserId);
    }

    @Test
    void viewPeersMaterials_PeerNotFound() {
        // Arrange
        UUID peerUserId = UUID.randomUUID();
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> expectedResponse = 
            ApiResponse.error("No user exists with this id", HttpStatus.NOT_FOUND);
        when(findMaterialsService.findPeerMaterials(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialController.viewPeersMaterials(peerUserId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No user exists with this id", response.getBody().getError());
        verify(findMaterialsService, times(1)).findPeerMaterials(peerUserId);
    }

    @Test
    void viewPeersMaterials_UserNotAuthenticated() {
        // Arrange
        UUID peerUserId = UUID.randomUUID();
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> expectedResponse = 
            ApiResponse.error("Not logged in", HttpStatus.FORBIDDEN);
        when(findMaterialsService.findPeerMaterials(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            findMaterialController.viewPeersMaterials(peerUserId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not logged in", response.getBody().getError());
        verify(findMaterialsService, times(1)).findPeerMaterials(peerUserId);
    }
} 