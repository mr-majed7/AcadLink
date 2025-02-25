package com.majed.acadlink.service;

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
import com.majed.acadlink.utility.AuthorizationCheck;
import com.majed.acadlink.utility.GetUserUtil;
import com.majed.acadlink.utility.SaveMaterialUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing materials.
 */
@Service
@Slf4j

public class MaterialService {
    private final FolderRepo folderRepo;
    private final MaterialsRepo materialsRepo;
    private final SaveMaterialUtil saveMaterialUtil;
    private final AuthorizationCheck authorizationCheck;
    private final GetUserUtil getUserUtil;

    /**
     * Constructor for MaterialService.
     *
     * @param folderRepo         the folder repository
     * @param materialsRepo      the materials repository
     * @param saveMaterialUtil   utility to save material files and links
     * @param authorizationCheck utility to check user authorization
     * @param getUserUtil        utility to get current user
     */
    public MaterialService(
            FolderRepo folderRepo,
            MaterialsRepo materialsRepo,
            SaveMaterialUtil saveMaterialUtil,
            AuthorizationCheck authorizationCheck,
            GetUserUtil getUserUtil
    ) {
        this.folderRepo = folderRepo;
        this.materialsRepo = materialsRepo;
        this.saveMaterialUtil = saveMaterialUtil;
        this.authorizationCheck = authorizationCheck;
        this.getUserUtil = getUserUtil;
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
            if (materialData.getFile() != null && !materialData.getFile().isEmpty()) {
                MaterialResponseDTO savedMaterial = saveMaterialUtil.saveMaterialFile(materialData);
                return ApiResponse.success(savedMaterial, HttpStatus.CREATED);
            } else if (materialData.getLink() != null && !materialData.getLink().isEmpty()) {
                MaterialResponseDTO savedMaterial = saveMaterialUtil.saveMaterialLink(materialData);
                return ApiResponse.success(savedMaterial, HttpStatus.CREATED);
            } else {
                return ApiResponse.error("File or Link is required", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            log.error(e.toString());
            return ApiResponse.error("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
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
        Optional<Folder> folder = folderRepo.findById(material.get().getFolder().getId());
        if (folder.isEmpty()) {
            return ApiResponse.error("No folder found associated with the material", HttpStatus.NOT_FOUND);
        }
        if (!authorizationCheck.checkAuthorization(folder.get().getUser().getId())) {
            return ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
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
        List<Materials> materials = materialsRepo.findByFolderIdAndType(folderId, type);
        Optional<Folder> folder = folderRepo.findById(folderId);
        if (folder.isEmpty()) {
            return ApiResponse.error("No folder found associated with the material", HttpStatus.NOT_FOUND);
        }
        if (!authorizationCheck.checkAuthorization(folder.get().getUser().getId())) {
            return ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
        }
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
        Optional<Materials> material = materialsRepo.findById(materialId);
        if (material.isEmpty()) {
            return ApiResponse.error("No material found with this id", HttpStatus.NOT_FOUND);
        }
        Optional<Folder> folder = folderRepo.findById(material.get().getFolder().getId());
        if (folder.isEmpty()) {
            return ApiResponse.error("No folder found associated with the material", HttpStatus.NOT_FOUND);
        }
        if (!authorizationCheck.checkAuthorization(folder.get().getUser().getId())) {
            return ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
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
            final String FILE_STORAGE_PATH = "/home/majed/AcadLink/backend/acadlink/storage/materials";
            MultipartFile file = newData.getFile();
            String filePath = String.valueOf(Paths.get(FILE_STORAGE_PATH, file.getOriginalFilename()));
            Files.createDirectories(Paths.get(FILE_STORAGE_PATH));
            file.transferTo(new File(filePath));
            current.setLink(filePath);
        }

        materialsRepo.save(current);
        MaterialResponseDTO response = new MaterialResponseDTO(current.getId(), current.getName(), current.getLink(),
                current.getType(), current.getPrivacy(), current.getFolder().getId()
        );
        return ApiResponse.success(response, HttpStatus.CREATED);
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
        Optional<Folder> folder = folderRepo.findById(material.get().getFolder().getId());
        if (folder.isEmpty()) {
            return ApiResponse.error("No folder found associated with the material", HttpStatus.NOT_FOUND);
        }
        if (!authorizationCheck.checkAuthorization(folder.get().getUser().getId())) {
            return ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
        }
        materialsRepo.delete(material.get());
        return ApiResponse.success(true, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> findMaterials(
            String keyWords
    ) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();

        if (user.isEmpty()) {
            return ApiResponse.error("User not found", HttpStatus.BAD_REQUEST);
        }

        List<MaterialResponseDTO> materials = new ArrayList<>();

        materials.addAll(
                materialsRepo.searchPublicMaterials(keyWords, Privacy.PUBLIC).stream().map(
                        value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(),
                                value.getType(), value.getPrivacy(), value.getFolder().getId())).toList());


        materials.addAll(
                materialsRepo.searchPeerMaterials(keyWords, user.get().getId(), Privacy.PEERS).stream().map(
                        value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(),
                                value.getType(), value.getPrivacy(), value.getFolder().getId())).toList());


        materials.addAll(
                materialsRepo.searchInstitutionalMaterials(keyWords, user.get().getInstitute(), Privacy.INSTITUTIONAL).
                        stream().map(
                                value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(),
                                        value.getType(), value.getPrivacy(), value.getFolder().getId())).toList());


        return ApiResponse.success(materials, HttpStatus.OK);
    }
}
