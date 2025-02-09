package com.majed.acadlink.service;

import com.majed.acadlink.domain.entitie.Materials;
import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.repository.FolderRepo;
import com.majed.acadlink.repository.MaterialsRepo;
import com.majed.acadlink.utility.SaveMaterialUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        return material.map(value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(),
                value.getType(), value.getPrivacy(), value.getFolder().getId())).orElse(null);
    }

    public List<MaterialResponseDTO> findMaterialByType(MaterialType type, UUID folderId) {
        List<Materials> materials = materialsRepo.findByFolderIdAndType(folderId, type);
        return materials.stream().map(value -> new MaterialResponseDTO(value.getId(), value.getName(),
                value.getLink(), value.getType(), value.getPrivacy(), value.getFolder().getId())).collect(Collectors.toList());
    }

}

