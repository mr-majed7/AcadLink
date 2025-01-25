package com.majed.acadlink.utility;

import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.entitie.Folder;
import com.majed.acadlink.entitie.Materials;
import com.majed.acadlink.repository.FolderRepo;
import com.majed.acadlink.repository.MaterialsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Component
public class SaveMaterialUtil {
    private final String FILE_STORAGE_PATH = "/home/majed/AcadLink/backend/acadlink/storage/materials";
    @Autowired
    private FolderRepo folderRepo;

    @Autowired
    private MaterialsRepo materialsRepo;


    public MaterialResponseDTO saveMaterialFile(MaterialAddDTO materialData) throws IOException {
        MultipartFile file = materialData.getFile();
        String filePath = String.valueOf(Paths.get(FILE_STORAGE_PATH, file.getOriginalFilename()));
        Files.createDirectories(Paths.get(FILE_STORAGE_PATH));
        file.transferTo(new File(filePath));

        return saveMaterialEntity(materialData, filePath);
    }

    public MaterialResponseDTO saveMaterialLink(MaterialAddDTO materialData) {
        String link = materialData.getLink();
        return saveMaterialEntity(materialData, link);
    }

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
