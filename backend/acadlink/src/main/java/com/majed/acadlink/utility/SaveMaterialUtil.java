package com.majed.acadlink.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.majed.acadlink.config.StorageConfig;
import com.majed.acadlink.domain.entity.Folder;
import com.majed.acadlink.domain.entity.Materials;
import com.majed.acadlink.domain.repository.FolderRepo;
import com.majed.acadlink.domain.repository.MaterialsRepo;
import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.Privacy;
import com.majed.acadlink.exception.MaterialSaveException;
import com.majed.acadlink.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for saving material files and links.
 * This class handles:
 * 1. File storage and management
 * 2. Link validation and storage
 * 3. Material response generation
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SaveMaterialUtil {
    private final StorageConfig storageConfig;
    private final FolderRepo folderRepo;
    private final MaterialsRepo materialsRepo;

    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unnamed_file";
        }
        // Remove any path components and keep only the filename
        String sanitized = filename.replaceAll("[\\\\/:*?\"<>|]", "_");
        // If the filename is empty after sanitization, use a default name
        return sanitized.isEmpty() ? "unnamed_file" : sanitized;
    }

    /**
     * Saves a material file to the configured storage path.
     * The file is stored with its original name in the materials directory.
     *
     * @param materialData the material data containing the file to save
     * @return the response DTO containing the saved material information
     * @throws IOException if an error occurs during file operations
     */
    public MaterialResponseDTO saveMaterialFile(MaterialAddDTO materialData) throws IOException {
        try {
            // Validate folder exists
            Folder folder = folderRepo.findById(materialData.getFolderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

            MultipartFile file = materialData.getFile();
            String storagePath = storageConfig.getMaterials().getPath();

            // Create storage directory if it doesn't exist
            Path storageDir = Paths.get(storagePath);
            Files.createDirectories(storageDir);

            // Generate unique filename with sanitized original name
            String originalFilename = file.getOriginalFilename();
            String sanitizedFilename = sanitizeFilename(originalFilename);
            String uniqueFilename = UUID.randomUUID() + "_" + sanitizedFilename;
            Path filePath = storageDir.resolve(uniqueFilename);

            // Save the file
            file.transferTo(filePath.toFile());

            // Save to database
            Materials material = new Materials();
            material.setFolder(folder);
            material.setName(sanitizedFilename);  // Use sanitized name for display
            material.setLink(filePath.toString());
            material.setType(materialData.getType());
            material.setPrivacy(materialData.getPrivacy() != null ? materialData.getPrivacy() : Privacy.PUBLIC);

            Materials savedMaterial = materialsRepo.save(material);
            if (savedMaterial == null) {
                throw new MaterialSaveException("Failed to save material to database");
            }

            return new MaterialResponseDTO(
                    savedMaterial.getId(),
                    savedMaterial.getName(),
                    savedMaterial.getLink(),
                    savedMaterial.getType(),
                    savedMaterial.getPrivacy(),
                    savedMaterial.getFolder().getId()
            );
        } catch (IOException e) {
            throw new MaterialSaveException("Failed to save material file", e);
        }
    }

    /**
     * Saves a material link.
     * Validates the link and creates a response DTO.
     *
     * @param materialData the material data containing the link to save
     * @return the response DTO containing the saved material information
     */
    public MaterialResponseDTO saveMaterialLink(MaterialAddDTO materialData) {
        // Validate folder exists
        Folder folder = folderRepo.findById(materialData.getFolderId())
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        // Save to database
        Materials material = new Materials();
        material.setFolder(folder);
        material.setName(materialData.getName());
        material.setLink(materialData.getLink());
        material.setType(materialData.getType());
        material.setPrivacy(materialData.getPrivacy() != null ? materialData.getPrivacy() : Privacy.PUBLIC);

        Materials savedMaterial = materialsRepo.save(material);
        if (savedMaterial == null) {
            throw new MaterialSaveException("Failed to save material to database");
        }

        return new MaterialResponseDTO(
                savedMaterial.getId(),
                savedMaterial.getName(),
                savedMaterial.getLink(),
                savedMaterial.getType(),
                savedMaterial.getPrivacy(),
                savedMaterial.getFolder().getId()
        );
    }
}
