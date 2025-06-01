package com.majed.acadlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.majed.acadlink.domain.entitie.Folder;
import com.majed.acadlink.domain.entitie.Materials;
import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.FolderRepo;
import com.majed.acadlink.domain.repository.MaterialsRepo;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.enums.Privacy;
import com.majed.acadlink.exception.MaterialOperationException;
import com.majed.acadlink.exception.MaterialSaveException;
import com.majed.acadlink.exception.ResourceNotFoundException;
import com.majed.acadlink.utility.AuthorizationCheck;
import com.majed.acadlink.utility.GetUserUtil;
import com.majed.acadlink.utility.SaveMaterialUtil;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private FolderRepo folderRepo;

    @Mock
    private MaterialsRepo materialsRepo;

    @Mock
    private SaveMaterialUtil saveMaterialUtil;

    @Mock
    private AuthorizationCheck authorizationCheck;

    @Mock
    private GetUserUtil getUserUtil;

    @InjectMocks
    private MaterialService materialService;

    private UUID testUserId;
    private UUID testFolderId;
    private UUID testMaterialId;
    private User testUser;
    private Folder testFolder;
    private Materials testMaterial;
    private MaterialAddDTO materialAddDTO;
    private MaterialResponseDTO materialResponseDTO;
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testFolderId = UUID.randomUUID();
        testMaterialId = UUID.randomUUID();

        // Setup test user
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setInstitute("Test Institute");

        // Setup test folder
        testFolder = new Folder();
        testFolder.setId(testFolderId);
        testFolder.setName("Test Folder");
        testFolder.setUser(testUser);

        // Setup test material
        testMaterial = new Materials();
        testMaterial.setId(testMaterialId);
        testMaterial.setName("Test Material");
        testMaterial.setLink("http://example.com/test.pdf");
        testMaterial.setType(MaterialType.BOOK);
        testMaterial.setPrivacy(Privacy.PUBLIC);
        testMaterial.setFolder(testFolder);

        // Setup material add DTO
        materialAddDTO = new MaterialAddDTO();
        materialAddDTO.setName("Test Material");
        materialAddDTO.setFolderId(testFolderId);
        materialAddDTO.setType(MaterialType.BOOK);
        materialAddDTO.setPrivacy(Privacy.PUBLIC);
        testFile = new MockMultipartFile(
            "file",
            "test.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "test content".getBytes()
        );
        materialAddDTO.setFile(testFile);

        // Setup material response DTO
        materialResponseDTO = new MaterialResponseDTO(
            testMaterialId,
            "Test Material",
            "http://example.com/test.pdf",
            MaterialType.BOOK,
            Privacy.PUBLIC,
            testFolderId
        );
    }

    @Test
    void saveMaterial_WithFile_Success() throws IOException {
        // Arrange
        when(folderRepo.existsById(testFolderId)).thenReturn(true);
        when(saveMaterialUtil.saveMaterialFile(any(MaterialAddDTO.class)))
            .thenReturn(materialResponseDTO);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialService.saveMaterial(materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(materialResponseDTO, response.getBody().getData());
        verify(folderRepo, times(1)).existsById(testFolderId);
        verify(saveMaterialUtil, times(1)).saveMaterialFile(materialAddDTO);
    }

    @Test
    void saveMaterial_WithLink_Success() {
        // Arrange
        materialAddDTO.setFile(null);
        materialAddDTO.setLink("http://example.com/test.pdf");
        when(folderRepo.existsById(testFolderId)).thenReturn(true);
        when(saveMaterialUtil.saveMaterialLink(any(MaterialAddDTO.class)))
            .thenReturn(materialResponseDTO);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialService.saveMaterial(materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(materialResponseDTO, response.getBody().getData());
        verify(folderRepo, times(1)).existsById(testFolderId);
        verify(saveMaterialUtil, times(1)).saveMaterialLink(materialAddDTO);
    }

    @Test
    void saveMaterial_NoFileOrLink() throws IOException {
        // Arrange
        materialAddDTO.setFile(null);
        materialAddDTO.setLink(null);
        when(folderRepo.existsById(testFolderId)).thenReturn(true);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialService.saveMaterial(materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("File or Link is required", response.getBody().getError());
        verify(folderRepo, times(1)).existsById(testFolderId);
        verify(saveMaterialUtil, times(0)).saveMaterialFile(any());
        verify(saveMaterialUtil, times(0)).saveMaterialLink(any());
    }

    @Test
    void saveMaterial_FolderNotFound() {
        // Arrange
        when(folderRepo.existsById(testFolderId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            materialService.saveMaterial(materialAddDTO));
        verify(folderRepo, times(1)).existsById(testFolderId);
    }

    @Test
    void findMaterial_Success() {
        // Arrange
        when(materialsRepo.findById(testMaterialId)).thenReturn(Optional.of(testMaterial));
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(true);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialService.findMaterial(testMaterialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(testMaterialId, response.getBody().getData().getId());
        assertEquals("Test Material", response.getBody().getData().getName());
        verify(materialsRepo, times(1)).findById(testMaterialId);
    }

    @Test
    void findMaterial_NotFound() {
        // Arrange
        when(materialsRepo.findById(testMaterialId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialService.findMaterial(testMaterialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No material found", response.getBody().getError());
        verify(materialsRepo, times(1)).findById(testMaterialId);
    }

    @Test
    void findMaterial_FolderNotFound() {
        // Arrange
        when(materialsRepo.findById(testMaterialId)).thenReturn(Optional.of(testMaterial));
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> materialService.findMaterial(testMaterialId));
        verify(materialsRepo, times(1)).findById(testMaterialId);
        verify(folderRepo, times(1)).findById(testFolderId);
    }

    @Test
    void findMaterial_NotAuthorized() {
        // Arrange
        when(materialsRepo.findById(testMaterialId)).thenReturn(Optional.of(testMaterial));
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialService.findMaterial(testMaterialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(materialsRepo, times(1)).findById(testMaterialId);
        verify(folderRepo, times(1)).findById(testFolderId);
    }

    @Test
    void findMaterialsByType_Success() {
        // Arrange
        MaterialType type = MaterialType.BOOK;
        List<Materials> materials = Arrays.asList(testMaterial);
        when(materialsRepo.findByFolderIdAndType(testFolderId, type)).thenReturn(materials);
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(true);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            materialService.findMaterialsByType(type, testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());
        
        MaterialResponseDTO materialResponse = response.getBody().getData().get(0);
        assertEquals(testMaterialId, materialResponse.getId());
        assertEquals(testMaterial.getName(), materialResponse.getName());
        assertEquals(testMaterial.getLink(), materialResponse.getLink());
        assertEquals(testMaterial.getType(), materialResponse.getType());
        assertEquals(testMaterial.getPrivacy(), materialResponse.getPrivacy());
        assertEquals(testFolderId, materialResponse.getFolderId());
        
        verify(materialsRepo, times(1)).findByFolderIdAndType(testFolderId, type);
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(authorizationCheck, times(1)).checkAuthorization(testUserId);
    }

    @Test
    void findMaterialsByType_FolderNotFound() {
        // Arrange
        MaterialType type = MaterialType.BOOK;
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            materialService.findMaterialsByType(type, testFolderId));
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(materialsRepo, times(0)).findByFolderIdAndType(testFolderId, type);
    }

    @Test
    void findMaterialsByType_NotAuthorized() {
        // Arrange
        MaterialType type = MaterialType.BOOK;
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> response = 
            materialService.findMaterialsByType(type, testFolderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(authorizationCheck, times(1)).checkAuthorization(testUserId);
        verify(materialsRepo, times(0)).findByFolderIdAndType(testFolderId, type);
    }

    @Test
    void updateMaterial_Success() throws IOException {
        // Arrange
        MaterialAddDTO updateDTO = new MaterialAddDTO();
        updateDTO.setName("Updated Material");
        updateDTO.setType(MaterialType.LECTURE_NOTE);
        updateDTO.setPrivacy(Privacy.PEERS);

        Materials updatedMaterial = new Materials();
        updatedMaterial.setId(testMaterialId);
        updatedMaterial.setName("Updated Material");
        updatedMaterial.setLink("http://example.com/updated.pdf");
        updatedMaterial.setType(MaterialType.LECTURE_NOTE);
        updatedMaterial.setPrivacy(Privacy.PEERS);
        updatedMaterial.setFolder(testFolder);

        when(materialsRepo.findById(testMaterialId)).thenReturn(Optional.of(testMaterial));
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(true);
        when(materialsRepo.save(any(Materials.class))).thenReturn(updatedMaterial);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialService.updateMaterial(testMaterialId, updateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(testMaterialId, response.getBody().getData().getId());
        assertEquals("Updated Material", response.getBody().getData().getName());
        assertEquals(MaterialType.LECTURE_NOTE, response.getBody().getData().getType());
        assertEquals(Privacy.PEERS, response.getBody().getData().getPrivacy());
        verify(materialsRepo, times(1)).findById(testMaterialId);
        verify(materialsRepo, times(1)).save(any(Materials.class));
    }

    @Test
    void updateMaterial_NotFound() throws IOException {
        // Arrange
        when(materialsRepo.findById(testMaterialId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialService.updateMaterial(testMaterialId, materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No material found with this id", response.getBody().getError());
        verify(materialsRepo, times(1)).findById(testMaterialId);
        verify(materialsRepo, times(0)).save(any());
    }

    @Test
    void updateMaterial_FolderNotFound() throws IOException {
        // Arrange
        when(materialsRepo.findById(testMaterialId)).thenReturn(Optional.of(testMaterial));
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            materialService.updateMaterial(testMaterialId, materialAddDTO));
        verify(materialsRepo, times(1)).findById(testMaterialId);
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(materialsRepo, times(0)).save(any());
    }

    @Test
    void updateMaterial_NotAuthorized() throws IOException {
        // Arrange
        when(materialsRepo.findById(testMaterialId)).thenReturn(Optional.of(testMaterial));
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialService.updateMaterial(testMaterialId, materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(materialsRepo, times(1)).findById(testMaterialId);
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(materialsRepo, times(0)).save(any());
    }

    @Test
    void deleteMaterial_Success() {
        // Arrange
        when(materialsRepo.findById(testMaterialId)).thenReturn(Optional.of(testMaterial));
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(true);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            materialService.deleteMaterial(testMaterialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().getData());
        verify(materialsRepo, times(1)).findById(testMaterialId);
        verify(materialsRepo, times(1)).delete(testMaterial);
    }

    @Test
    void deleteMaterial_NotFound() {
        // Arrange
        when(materialsRepo.findById(testMaterialId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            materialService.deleteMaterial(testMaterialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No material found to delete", response.getBody().getError());
        verify(materialsRepo, times(1)).findById(testMaterialId);
        verify(materialsRepo, times(0)).delete(any());
    }

    @Test
    void deleteMaterial_FolderNotFound() {
        // Arrange
        when(materialsRepo.findById(testMaterialId)).thenReturn(Optional.of(testMaterial));
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> materialService.deleteMaterial(testMaterialId));
        verify(materialsRepo, times(1)).findById(testMaterialId);
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(materialsRepo, times(0)).delete(any());
    }

    @Test
    void deleteMaterial_NotAuthorized() {
        // Arrange
        when(materialsRepo.findById(testMaterialId)).thenReturn(Optional.of(testMaterial));
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(authorizationCheck.checkAuthorization(testUserId)).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response = 
            materialService.deleteMaterial(testMaterialId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not authorized", response.getBody().getError());
        verify(materialsRepo, times(1)).findById(testMaterialId);
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(materialsRepo, times(0)).delete(any());
    }

    @Test
    void saveMaterial_Success_File() throws IOException {
        // Arrange
        MaterialAddDTO materialData = new MaterialAddDTO();
        materialData.setName("test.pdf");
        materialData.setType(MaterialType.BOOK);
        materialData.setFolderId(testFolderId);
        materialData.setFile(testFile);

        MaterialResponseDTO expectedResponse = new MaterialResponseDTO(
            testMaterialId,
            "test.pdf",
            "http://example.com/test.pdf",
            MaterialType.BOOK,
            Privacy.PUBLIC,
            testFolderId
        );

        when(folderRepo.existsById(testFolderId)).thenReturn(true);
        when(saveMaterialUtil.saveMaterialFile(any(MaterialAddDTO.class)))
            .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<MaterialResponseDTO>> response = 
            materialService.saveMaterial(materialData);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(expectedResponse, response.getBody().getData());
        verify(folderRepo, times(1)).existsById(testFolderId);
        verify(saveMaterialUtil, times(1)).saveMaterialFile(materialData);
    }

    @Test
    void saveMaterial_IOException() throws IOException {
        // Arrange
        when(folderRepo.existsById(testFolderId)).thenReturn(true);
        when(saveMaterialUtil.saveMaterialFile(any(MaterialAddDTO.class)))
            .thenThrow(new MaterialSaveException("Failed to save material file", new IOException("Test IO error")));

        // Act & Assert
        MaterialOperationException exception = assertThrows(MaterialOperationException.class, () -> 
            materialService.saveMaterial(materialAddDTO));
        assertEquals("Failed to save material", exception.getMessage());
        assertTrue(exception.getCause() instanceof MaterialSaveException);
        assertEquals("Failed to save material file", exception.getCause().getMessage());
        assertTrue(exception.getCause().getCause() instanceof IOException);
        assertEquals("Test IO error", exception.getCause().getCause().getMessage());
        verify(folderRepo, times(1)).existsById(testFolderId);
        verify(saveMaterialUtil, times(1)).saveMaterialFile(materialAddDTO);
    }
} 