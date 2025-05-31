package com.majed.acadlink.api.v1.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import org.springframework.mock.web.MockMultipartFile;

import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.enums.Privacy;
import com.majed.acadlink.service.MaterialService;

@ExtendWith(MockitoExtension.class)
class MaterialsControllerTest {

    @Mock
    private MaterialService materialService;

    @InjectMocks
    private MaterialsController materialsController;

    private MaterialResponseDTO sampleMaterial1;
    private MaterialResponseDTO sampleMaterial2;
    private List<MaterialResponseDTO> sampleMaterials;
    private UUID testUserId;
    private UUID testFolderId;
    private MaterialAddDTO materialAddDTO;
    private MockMultipartFile sampleFile;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testFolderId = UUID.randomUUID();

        // Setup sample materials
        sampleMaterial1 = new MaterialResponseDTO(
            UUID.randomUUID(),
            "Sample Material 1",
            "http://example.com/material1",
            MaterialType.BOOK,
            Privacy.PUBLIC,
            testFolderId
        );

        sampleMaterial2 = new MaterialResponseDTO(
            UUID.randomUUID(),
            "Sample Material 2",
            "http://example.com/material2",
            MaterialType.LECTURE_SLIDE,
            Privacy.PEERS,
            testFolderId
        );

        sampleMaterials = Arrays.asList(sampleMaterial1, sampleMaterial2);

        // Setup sample file
        sampleFile = new MockMultipartFile(
            "file",
            "test.pdf",
            "application/pdf",
            "test content".getBytes()
        );

        // Setup sample material add DTO
        materialAddDTO = new MaterialAddDTO();
        materialAddDTO.setName("Test Material");
        materialAddDTO.setFolderId(testFolderId);
        materialAddDTO.setType(MaterialType.BOOK);
        materialAddDTO.setPrivacy(Privacy.PUBLIC);
        materialAddDTO.setFile(sampleFile);
    }

    @Test
    void addMaterials_WithFile_Success() {
        // Arrange
        MaterialResponseDTO savedMaterial = new MaterialResponseDTO(
            UUID.randomUUID(),
            "Test Material",
            "http://example.com/test.pdf",
            MaterialType.BOOK,
            Privacy.PUBLIC,
            testFolderId
        );
        ResponseEntity<ApiResponse<MaterialResponseDTO>> expectedResponse = 
            ApiResponse.success(savedMaterial, HttpStatus.CREATED);
        when(materialService.saveMaterial(any(MaterialAddDTO.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialsController.addMaterials(materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedMaterial, response.getBody().getData());
        verify(materialService, times(1)).saveMaterial(argThat(dto -> 
            dto.getName().equals(materialAddDTO.getName()) &&
            dto.getFolderId().equals(materialAddDTO.getFolderId()) &&
            dto.getType().equals(materialAddDTO.getType()) &&
            dto.getPrivacy().equals(materialAddDTO.getPrivacy()) &&
            dto.getFile() != null
        ));
    }

    @Test
    void addMaterials_WithLink_Success() {
        // Arrange
        materialAddDTO.setFile(null);
        materialAddDTO.setLink("http://example.com/test");
        MaterialResponseDTO savedMaterial = new MaterialResponseDTO(
            UUID.randomUUID(),
            "Test Material",
            "http://example.com/test",
            MaterialType.BOOK,
            Privacy.PUBLIC,
            testFolderId
        );
        ResponseEntity<ApiResponse<MaterialResponseDTO>> expectedResponse = 
            ApiResponse.success(savedMaterial, HttpStatus.CREATED);
        when(materialService.saveMaterial(any(MaterialAddDTO.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialsController.addMaterials(materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedMaterial, response.getBody().getData());
        verify(materialService, times(1)).saveMaterial(argThat(dto -> 
            dto.getName().equals(materialAddDTO.getName()) &&
            dto.getFolderId().equals(materialAddDTO.getFolderId()) &&
            dto.getType().equals(materialAddDTO.getType()) &&
            dto.getPrivacy().equals(materialAddDTO.getPrivacy()) &&
            dto.getLink().equals(materialAddDTO.getLink())
        ));
    }

    @Test
    void addMaterials_NoFileOrLink() {
        // Arrange
        materialAddDTO.setFile(null);
        materialAddDTO.setLink(null);
        ResponseEntity<ApiResponse<MaterialResponseDTO>> expectedResponse = 
            ApiResponse.error("File or Link is required", HttpStatus.BAD_REQUEST);
        when(materialService.saveMaterial(any(MaterialAddDTO.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialsController.addMaterials(materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("File or Link is required", response.getBody().getError());
        verify(materialService, times(1)).saveMaterial(materialAddDTO);
    }

    @Test
    void addMaterials_IOException() {
        // Arrange
        when(materialService.saveMaterial(any(MaterialAddDTO.class)))
            .thenReturn(ApiResponse.error("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR));

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialsController.addMaterials(materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().getError());
        verify(materialService, times(1)).saveMaterial(materialAddDTO);
    }

    @Test
    void getMaterial_Success() {
        // Arrange
        UUID materialId = UUID.randomUUID();
        ResponseEntity<ApiResponse<MaterialResponseDTO>> expectedResponse = 
            ApiResponse.success(sampleMaterial1, HttpStatus.OK);
        when(materialService.findMaterial(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialsController.getMaterial(materialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleMaterial1, response.getBody().getData());
        verify(materialService, times(1)).findMaterial(materialId);
    }

    @Test
    void getMaterial_Unauthorized() {
        // Arrange
        UUID materialId = UUID.randomUUID();
        ResponseEntity<ApiResponse<MaterialResponseDTO>> expectedResponse = 
            ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
        when(materialService.findMaterial(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialsController.getMaterial(materialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(materialService, times(1)).findMaterial(materialId);
    }

    @Test
    void getMaterial_FolderNotFound() {
        // Arrange
        UUID materialId = UUID.randomUUID();
        ResponseEntity<ApiResponse<MaterialResponseDTO>> expectedResponse = 
            ApiResponse.error("No folder found associated with the material", HttpStatus.NOT_FOUND);
        when(materialService.findMaterial(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialsController.getMaterial(materialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No folder found associated with the material", response.getBody().getError());
        verify(materialService, times(1)).findMaterial(materialId);
    }

    @Test
    void getMaterialByType_Success() {
        // Arrange
        MaterialType type = MaterialType.BOOK;
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> expectedResponse = 
            ApiResponse.success(sampleMaterials, HttpStatus.OK);
        when(materialService.findMaterialsByType(any(MaterialType.class), any(UUID.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            materialsController.getMaterialByType(type, testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleMaterials, response.getBody().getData());
        verify(materialService, times(1)).findMaterialsByType(type, testFolderId);
    }

    @Test
    void getMaterialByType_NoResults() {
        // Arrange
        MaterialType type = MaterialType.LECTURE_NOTE;
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> expectedResponse = 
            ApiResponse.success(List.of(), HttpStatus.OK);
        when(materialService.findMaterialsByType(any(MaterialType.class), any(UUID.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            materialsController.getMaterialByType(type, testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(List.of(), response.getBody().getData());
        verify(materialService, times(1)).findMaterialsByType(type, testFolderId);
    }

    @Test
    void getMaterialByType_Unauthorized() {
        // Arrange
        MaterialType type = MaterialType.BOOK;
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> expectedResponse = 
            ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
        when(materialService.findMaterialsByType(any(MaterialType.class), any(UUID.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            materialsController.getMaterialByType(type, testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(materialService, times(1)).findMaterialsByType(type, testFolderId);
    }

    @Test
    void getMaterialByType_FolderNotFound() {
        // Arrange
        MaterialType type = MaterialType.BOOK;
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> expectedResponse = 
            ApiResponse.error("No folder found associated with the material", HttpStatus.NOT_FOUND);
        when(materialService.findMaterialsByType(any(MaterialType.class), any(UUID.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            materialsController.getMaterialByType(type, testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No folder found associated with the material", response.getBody().getError());
        verify(materialService, times(1)).findMaterialsByType(type, testFolderId);
    }

    @Test
    void updateMaterial_Success() throws IOException {
        // Arrange
        UUID materialId = UUID.randomUUID();
        MaterialResponseDTO updatedMaterial = new MaterialResponseDTO(
            materialId,
            "Updated Material",
            "http://example.com/updated.pdf",
            MaterialType.BOOK,
            Privacy.PUBLIC,
            testFolderId
        );
        ResponseEntity<ApiResponse<MaterialResponseDTO>> expectedResponse = 
            ApiResponse.success(updatedMaterial, HttpStatus.CREATED);
        when(materialService.updateMaterial(any(UUID.class), any(MaterialAddDTO.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialsController.updateMaterial(materialId, materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedMaterial, response.getBody().getData());
        verify(materialService, times(1)).updateMaterial(
            eq(materialId),
            argThat(dto -> 
                dto.getName().equals(materialAddDTO.getName()) &&
                dto.getFolderId().equals(materialAddDTO.getFolderId()) &&
                dto.getType().equals(materialAddDTO.getType()) &&
                dto.getPrivacy().equals(materialAddDTO.getPrivacy()) &&
                dto.getFile() != null
            )
        );
    }

    @Test
    void updateMaterial_NotFound() throws IOException {
        // Arrange
        UUID materialId = UUID.randomUUID();
        ResponseEntity<ApiResponse<MaterialResponseDTO>> expectedResponse = 
            ApiResponse.error("No material found with this id", HttpStatus.NOT_FOUND);
        when(materialService.updateMaterial(any(UUID.class), any(MaterialAddDTO.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialsController.updateMaterial(materialId, materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No material found with this id", response.getBody().getError());
        verify(materialService, times(1)).updateMaterial(materialId, materialAddDTO);
    }

    @Test
    void updateMaterial_Unauthorized() throws IOException {
        // Arrange
        UUID materialId = UUID.randomUUID();
        ResponseEntity<ApiResponse<MaterialResponseDTO>> expectedResponse = 
            ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
        when(materialService.updateMaterial(any(UUID.class), any(MaterialAddDTO.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialsController.updateMaterial(materialId, materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(materialService, times(1)).updateMaterial(materialId, materialAddDTO);
    }

    @Test
    void updateMaterial_FolderNotFound() throws IOException {
        // Arrange
        UUID materialId = UUID.randomUUID();
        ResponseEntity<ApiResponse<MaterialResponseDTO>> expectedResponse = 
            ApiResponse.error("No folder found associated with the material", HttpStatus.NOT_FOUND);
        when(materialService.updateMaterial(any(UUID.class), any(MaterialAddDTO.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialsController.updateMaterial(materialId, materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No folder found associated with the material", response.getBody().getError());
        verify(materialService, times(1)).updateMaterial(materialId, materialAddDTO);
    }

    @Test
    void deleteMaterial_Success() {
        // Arrange
        UUID materialId = UUID.randomUUID();
        ResponseEntity<ApiResponse<Boolean>> expectedResponse = 
            ApiResponse.success(true, HttpStatus.OK);
        when(materialService.deleteMaterial(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            materialsController.deleteMaterial(materialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().getData());
        verify(materialService, times(1)).deleteMaterial(materialId);
    }

    @Test
    void deleteMaterial_NotFound() {
        // Arrange
        UUID materialId = UUID.randomUUID();
        ResponseEntity<ApiResponse<Boolean>> expectedResponse = 
            ApiResponse.error("No material found to delete", HttpStatus.NOT_FOUND);
        when(materialService.deleteMaterial(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            materialsController.deleteMaterial(materialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No material found to delete", response.getBody().getError());
        verify(materialService, times(1)).deleteMaterial(materialId);
    }

    @Test
    void deleteMaterial_Unauthorized() {
        // Arrange
        UUID materialId = UUID.randomUUID();
        ResponseEntity<ApiResponse<Boolean>> expectedResponse = 
            ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
        when(materialService.deleteMaterial(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            materialsController.deleteMaterial(materialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(materialService, times(1)).deleteMaterial(materialId);
    }

    @Test
    void deleteMaterial_FolderNotFound() {
        // Arrange
        UUID materialId = UUID.randomUUID();
        ResponseEntity<ApiResponse<Boolean>> expectedResponse = 
            ApiResponse.error("No folder found associated with the material", HttpStatus.NOT_FOUND);
        when(materialService.deleteMaterial(any(UUID.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            materialsController.deleteMaterial(materialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No folder found associated with the material", response.getBody().getError());
        verify(materialService, times(1)).deleteMaterial(materialId);
    }
} 