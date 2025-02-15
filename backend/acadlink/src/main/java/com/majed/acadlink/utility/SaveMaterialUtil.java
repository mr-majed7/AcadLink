package com.majed.acadlink.utility;

import com.majed.acadlink.domain.entitie.Folder;
import com.majed.acadlink.domain.entitie.Materials;
import com.majed.acadlink.domain.repository.FolderRepo;
import com.majed.acadlink.domain.repository.MaterialsRepo;
import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Utility class for saving material files and links.
 */
@Slf4j
@Component
public class SaveMaterialUtil {
    private static final String FILE_STORAGE_PATH = "/home/majed/AcadLink/backend/acadlink/storage/materials";
    private final FolderRepo folderRepo;
    private final MaterialsRepo materialsRepo;

    /**
     * Constructor for SaveMaterialUtil.
     *
     * @param folderRepo    the folder repository
     * @param materialsRepo the materials repository
     */
    public SaveMaterialUtil(FolderRepo folderRepo, MaterialsRepo materialsRepo) {
        this.folderRepo = folderRepo;
        this.materialsRepo = materialsRepo;
    }

    /**
     * Saves a material file to the storage and creates a material entity.
     *
     * @param materialData the material data transfer object containing file information
     * @return the response DTO containing saved material information
     * @throws IOException if an I/O error occurs during file saving
     */
    public MaterialResponseDTO saveMaterialFile(MaterialAddDTO materialData) throws IOException {
        MultipartFile file = materialData.getFile();
        String filePath = String.valueOf(Paths.get(FILE_STORAGE_PATH, file.getOriginalFilename()));
        Files.createDirectories(Paths.get(FILE_STORAGE_PATH));
        file.transferTo(new File(filePath));

        return saveMaterialEntity(materialData, filePath);
    }

    /**
     * Saves a material link and creates a material entity.
     *
     * @param materialData the material data transfer object containing link information
     * @return the response DTO containing saved material information
     */
    public MaterialResponseDTO saveMaterialLink(MaterialAddDTO materialData) {
        String link = materialData.getLink();
        return saveMaterialEntity(materialData, link);
    }

    /**
     * Saves a material entity to the database.
     *
     * @param materialData the material data transfer object
     * @param filePath     the file path or link of the material
     * @return the response DTO containing saved material information
     */
    private MaterialResponseDTO saveMaterialEntity(MaterialAddDTO materialData, String filePath) {
        Optional<Folder> folderOpt = folderRepo.findById(materialData.getFolderId());
        if (!folderOpt.isPresent()) {
            return null;
        }

        Folder folder = folderOpt.get();
        MaterialResponseDTO responseDTO = null;

        Materials material = new Materials();
        material.setFolder(folder);
        material.setName(materialData.getName());
        material.setLink(filePath);
        material.setType(materialData.getType());
        material.setPrivacy(materialData.getPrivacy());
        Materials savedMaterial = materialsRepo.save(material);
        responseDTO = new MaterialResponseDTO(
                savedMaterial.getId(), savedMaterial.getName(), savedMaterial.getLink(), savedMaterial.getType(),
                savedMaterial.getPrivacy(), savedMaterial.getFolder().getId()
        );

        return responseDTO;
    }
}
