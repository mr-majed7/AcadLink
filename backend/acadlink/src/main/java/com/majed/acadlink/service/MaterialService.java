package com.majed.acadlink.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.majed.acadlink.config.StorageConfig;
import com.majed.acadlink.domain.entity.Folder;
import com.majed.acadlink.domain.entity.Materials;
import com.majed.acadlink.domain.repository.FolderRepo;
import com.majed.acadlink.domain.repository.MaterialsRepo;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.exception.MaterialOperationException;
import com.majed.acadlink.exception.MaterialSaveException;
import com.majed.acadlink.exception.ResourceNotFoundException;
import com.majed.acadlink.utility.AuthorizationCheck;
import com.majed.acadlink.utility.SaveMaterialUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing materials.
 */
@Service
@Slf4j
public class MaterialService {
    private static final String NO_FOLDER_FOUND_MESSAGE = "No folder found associated with the material";
    private static final String NOT_AUTHORIZED_MESSAGE = "Not authorized";

    private final FolderRepo folderRepo;
    private final MaterialsRepo materialsRepo;
    private final SaveMaterialUtil saveMaterialUtil;
    private final AuthorizationCheck authorizationCheck;
    private final StorageConfig storageConfig;

    /**
     * Constructor for MaterialService.
     *
     * @param folderRepo         the folder repository
     * @param materialsRepo      the materials repository
     * @param saveMaterialUtil   utility to save material files and links
     * @param authorizationCheck utility to check user authorization
     * @param storageConfig      configuration for storage
     */
    public MaterialService(
            FolderRepo folderRepo,
            MaterialsRepo materialsRepo,
            SaveMaterialUtil saveMaterialUtil,
            AuthorizationCheck authorizationCheck,
            StorageConfig storageConfig
    ) {
        this.folderRepo = folderRepo;
        this.materialsRepo = materialsRepo;
        this.saveMaterialUtil = saveMaterialUtil;
        this.authorizationCheck = authorizationCheck;
        this.storageConfig = storageConfig;
    }

    /**
     * Saves a new material.
     *
     * @param materialData the data for the new material
     * @return the response entity containing the saved material or an error status
     */
    public ResponseEntity<ApiResponse<MaterialResponseDTO>> saveMaterial(
            MaterialAddDTO materialData) {
        try {
            // Validate folder exists
            if (!folderRepo.existsById(materialData.getFolderId())) {
                throw new ResourceNotFoundException("Folder not found");
            }

            if (materialData.getFile() != null && !materialData.getFile().isEmpty()) {
                MaterialResponseDTO savedMaterial = saveMaterialUtil.saveMaterialFile(materialData);
                return ApiResponse.success(savedMaterial, HttpStatus.CREATED);
            } else if (materialData.getLink() != null && !materialData.getLink().isEmpty()) {
                MaterialResponseDTO savedMaterial = saveMaterialUtil.saveMaterialLink(materialData);
                return ApiResponse.success(savedMaterial, HttpStatus.CREATED);
            } else {
                return ApiResponse.error("File or Link is required", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | MaterialSaveException e) {
            log.error("Error saving material: {}", e.getMessage(), e);
            throw new MaterialOperationException("Failed to save material", e);
        }
    }

    /**
     * Finds a material by ID.
     *
     * @param id the ID of the material to find
     * @return the response entity containing the material or an error status
     */
    public ResponseEntity<ApiResponse<MaterialResponseDTO>> findMaterial(UUID id) {
        Optional<Materials> material = materialsRepo.findById(id);
        if (material.isEmpty()) {
            return ApiResponse.error("No material found", HttpStatus.NOT_FOUND);
        }
        Folder folder = folderRepo.findById(material.get().getFolder().getId())
                .orElseThrow(() -> new ResourceNotFoundException(NO_FOLDER_FOUND_MESSAGE));
        if (!authorizationCheck.checkAuthorization(folder.getUser().getId())) {
            return ApiResponse.error(NOT_AUTHORIZED_MESSAGE, HttpStatus.FORBIDDEN);
        }
        MaterialResponseDTO materialResponse = material.map(
                value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(),
                        value.getType(), value.getPrivacy(), value.getFolder().getId())).orElse(null);
        return ApiResponse.success(materialResponse, HttpStatus.OK);
    }

    /**
     * Finds materials by type and folder ID.
     *
     * @param type     the type of the materials to find
     * @param folderId the ID of the folder containing the materials
     * @return the response entity containing the list of materials or an error status
     */
    public ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> findMaterialsByType(
            MaterialType type,
            UUID folderId) {
        Folder folder = folderRepo.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException(NO_FOLDER_FOUND_MESSAGE));
        if (!authorizationCheck.checkAuthorization(folder.getUser().getId())) {
            return ApiResponse.error(NOT_AUTHORIZED_MESSAGE, HttpStatus.FORBIDDEN);
        }
        List<Materials> materials = materialsRepo.findByFolderIdAndType(folderId, type);
        List<MaterialResponseDTO> materialList = materials.stream().map(
                value -> new MaterialResponseDTO(value.getId(), value.getName(),
                        value.getLink(), value.getType(), value.getPrivacy(), value.getFolder().getId())).toList();

        return ApiResponse.success(materialList, HttpStatus.OK);
    }

    /**
     * Updates a material by ID.
     *
     * @param materialId the ID of the material to update
     * @param newData    the new data for the material
     * @return the response entity containing the updated material or an error status
     * @throws IOException if an I/O error occurs
     */
    public ResponseEntity<ApiResponse<MaterialResponseDTO>> updateMaterial(
            UUID materialId, MaterialAddDTO newData) throws IOException {
        try {
            Optional<Materials> material = materialsRepo.findById(materialId);
            if (material.isEmpty()) {
                return ApiResponse.error("No material found with this id", HttpStatus.NOT_FOUND);
            }
            Folder folder = folderRepo.findById(material.get().getFolder().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(NO_FOLDER_FOUND_MESSAGE));
            if (!authorizationCheck.checkAuthorization(folder.getUser().getId())) {
                return ApiResponse.error(NOT_AUTHORIZED_MESSAGE, HttpStatus.FORBIDDEN);
            }

            Materials current = material.get();

            if (newData.getName() != null) {
                current.setName(newData.getName());
            }
            if (newData.getType() != null) {
                current.setType(newData.getType());
            }
            if (newData.getLink() != null) {
                current.setLink(newData.getLink());
            }
            if (newData.getPrivacy() != null) {
                current.setPrivacy(newData.getPrivacy());
            }
            if (newData.getFile() != null) {
                // Use SaveMaterialUtil to handle file upload securely
                MaterialAddDTO tempMaterialData = new MaterialAddDTO();
                tempMaterialData.setFile(newData.getFile());
                tempMaterialData.setFolderId(current.getFolder().getId());
                tempMaterialData.setType(current.getType());
                tempMaterialData.setPrivacy(current.getPrivacy());

                MaterialResponseDTO savedMaterial = saveMaterialUtil.saveMaterialFile(tempMaterialData);
                current.setLink(savedMaterial.getLink());
                current.setName(savedMaterial.getName());
            }

            materialsRepo.save(current);
            MaterialResponseDTO response = new MaterialResponseDTO(current.getId(), current.getName(), current.getLink(),
                    current.getType(), current.getPrivacy(), current.getFolder().getId()
            );
            return ApiResponse.success(response, HttpStatus.CREATED);
        } catch (MaterialSaveException e) {
            log.error("Error updating material: {}", e.getMessage(), e);
            throw new MaterialOperationException("Failed to update material", e);
        }
    }

    /**
     * Deletes a material by ID.
     *
     * @param id the ID of the material to delete
     * @return the response entity containing the deletion status
     */
    public ResponseEntity<ApiResponse<Boolean>> deleteMaterial(UUID id) {
        Optional<Materials> material = materialsRepo.findById(id);

        if (material.isEmpty()) {
            return ApiResponse.error("No material found to delete", HttpStatus.NOT_FOUND);
        }
        Folder folder = folderRepo.findById(material.get().getFolder().getId())
                .orElseThrow(() -> new ResourceNotFoundException(NO_FOLDER_FOUND_MESSAGE));
        if (!authorizationCheck.checkAuthorization(folder.getUser().getId())) {
            return ApiResponse.error(NOT_AUTHORIZED_MESSAGE, HttpStatus.FORBIDDEN);
        }
        materialsRepo.delete(material.get());
        return ApiResponse.success(true, HttpStatus.OK);
    }
}
