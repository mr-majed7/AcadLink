package com.majed.acadlink.service;

import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.entitie.Materials;
import com.majed.acadlink.repository.FolderRepo;
import com.majed.acadlink.repository.MaterialsRepo;
import com.majed.acadlink.utility.SaveMaterialUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class MaterialService {
    @Autowired
    private FolderRepo folderRepo;
    @Autowired
    private MaterialsRepo materialsRepo;
    @Autowired
    private SaveMaterialUtil saveMaterialUtil;

    public MaterialResponseDTO saveMaterial(MaterialAddDTO materialData) {
        try {
            if (materialData.getFile() != null && !materialData.getFile().isEmpty()) {
                return saveMaterialUtil.saveMaterialFile(materialData);
            } else if (materialData.getLink() != null && !materialData.getLink().isEmpty()) {
                return saveMaterialUtil.saveMaterialLink(materialData);
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }
    }


    public MaterialResponseDTO findMaterial(UUID id) {
        Optional<Materials> material = materialsRepo.findById(id);
        return material.map(value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(), value.getType(), value.getFolder().getId())).orElse(null);
    }

}

