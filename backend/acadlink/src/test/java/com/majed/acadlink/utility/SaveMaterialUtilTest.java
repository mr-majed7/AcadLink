package com.majed.acadlink.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.majed.acadlink.config.StorageConfig;
import com.majed.acadlink.domain.entity.Folder;
import com.majed.acadlink.domain.entity.Materials;
import com.majed.acadlink.domain.entity.User;
import com.majed.acadlink.domain.repository.FolderRepo;
import com.majed.acadlink.domain.repository.MaterialsRepo;
import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.enums.Privacy;
import com.majed.acadlink.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class SaveMaterialUtilTest {

    @TempDir
    Path tempDir;
    @Mock
    private FolderRepo folderRepo;
    @Mock
    private MaterialsRepo materialsRepo;
    @Mock
    private StorageConfig storageConfig;
    @Mock
    private StorageConfig.Materials materialsConfig;
    @InjectMocks
    private SaveMaterialUtil saveMaterialUtil;
    private UUID testUserId;
    private UUID testFolderId;
    private UUID testMaterialId;
    private User testUser;
    private Folder testFolder;
    private Materials testMaterial;
    private MaterialAddDTO materialAddDTO;
    private MockMultipartFile testFile;
    private String testStoragePath;

    @BeforeEach
    void setUp() throws IOException {
        testUserId = UUID.randomUUID();
        testFolderId = UUID.randomUUID();
        testMaterialId = UUID.randomUUID();
        testStoragePath = tempDir.toString();

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
        testMaterial.setName("test.pdf");
        testMaterial.setLink(testStoragePath + "/test.pdf");
        testMaterial.setType(MaterialType.BOOK);
        testMaterial.setPrivacy(Privacy.PUBLIC);
        testMaterial.setFolder(testFolder);

        // Setup material add DTO
        materialAddDTO = new MaterialAddDTO();
        materialAddDTO.setName("test.pdf");
        materialAddDTO.setFolderId(testFolderId);
        materialAddDTO.setType(MaterialType.BOOK);
        materialAddDTO.setPrivacy(Privacy.PUBLIC);
        testFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );
        materialAddDTO.setFile(testFile);

        // Setup storage config - use lenient since not all tests need it
        lenient().when(storageConfig.getMaterials()).thenReturn(materialsConfig);
        lenient().when(materialsConfig.getPath()).thenReturn(testStoragePath);
    }

    @Test
    void saveMaterialFile_Success() throws IOException {
        // Arrange
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(materialsRepo.save(any(Materials.class))).thenAnswer(invocation -> {
            Materials savedMaterial = invocation.getArgument(0);
            savedMaterial.setId(testMaterialId);
            return savedMaterial;
        });

        // Act
        MaterialResponseDTO response = saveMaterialUtil.saveMaterialFile(materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals("test.pdf", response.getName());
        assertEquals(MaterialType.BOOK, response.getType());
        assertEquals(Privacy.PUBLIC, response.getPrivacy());
        assertEquals(testFolderId, response.getFolderId());
        assertNotNull(response.getLink());
        assertTrue(response.getLink().contains(testStoragePath));
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(materialsRepo, times(1)).save(any(Materials.class));
    }

    @Test
    void saveMaterialFile_FolderNotFound() {
        // Arrange
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> saveMaterialUtil.saveMaterialFile(materialAddDTO));
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(materialsRepo, times(0)).save(any());
    }

    @Test
    void saveMaterialFile_IOException() throws IOException {
        // Arrange
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(materialsRepo.save(any(Materials.class))).thenThrow(new RuntimeException("Test error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> saveMaterialUtil.saveMaterialFile(materialAddDTO));
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(materialsRepo, times(1)).save(any(Materials.class));
    }

    @Test
    void saveMaterialLink_Success() {
        // Arrange
        materialAddDTO.setFile(null);
        materialAddDTO.setLink("http://example.com/test.pdf");
        materialAddDTO.setName("Test Material"); // Set name for link material
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.of(testFolder));
        when(materialsRepo.save(any(Materials.class))).thenAnswer(invocation -> {
            Materials savedMaterial = invocation.getArgument(0);
            savedMaterial.setId(testMaterialId);
            return savedMaterial;
        });

        // Act
        MaterialResponseDTO response = saveMaterialUtil.saveMaterialLink(materialAddDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Test Material", response.getName());
        assertEquals("http://example.com/test.pdf", response.getLink());
        assertEquals(MaterialType.BOOK, response.getType());
        assertEquals(Privacy.PUBLIC, response.getPrivacy());
        assertEquals(testFolderId, response.getFolderId());
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(materialsRepo, times(1)).save(any(Materials.class));
    }

    @Test
    void saveMaterialLink_FolderNotFound() {
        // Arrange
        materialAddDTO.setFile(null);
        materialAddDTO.setLink("http://example.com/test.pdf");
        when(folderRepo.findById(testFolderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> saveMaterialUtil.saveMaterialLink(materialAddDTO));
        verify(folderRepo, times(1)).findById(testFolderId);
        verify(materialsRepo, times(0)).save(any());
    }
} 