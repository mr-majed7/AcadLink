package com.majed.acadlink.controller;

import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.entitie.Folder;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.repository.FolderRepo;
import com.majed.acadlink.service.MaterialService;
import com.majed.acadlink.utility.AuthorizationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/material")
@Slf4j
public class MaterialsController {
    @Autowired
    private FolderRepo folderRepo;
    @Autowired
    private MaterialService materialService;
    @Autowired
    private AuthorizationCheck authorizationCheck;

    @PostMapping(value = "/add-material", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MaterialResponseDTO> addMaterials(@ModelAttribute MaterialAddDTO materialData) {
        try {
            MaterialResponseDTO addedMaterial = materialService.saveMaterial(materialData);
            return new ResponseEntity<>(addedMaterial, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/get-material-by-id/{id}")
    public ResponseEntity<MaterialResponseDTO> getMaterial(@PathVariable UUID id) {

        MaterialResponseDTO response = materialService.findMaterial(id);

        if (response != null) {
            Folder folder = folderRepo.findById(response.getFolderId()).get();
            if (authorizationCheck.checkAuthorization(folder.getUser().getId())) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-materials-by-type/{type}/{folder-id}")
    public ResponseEntity<List<MaterialResponseDTO>> getMaterialByType(@PathVariable MaterialType type,
                                                                       @PathVariable("folder-id") UUID folderId) {
        Optional<Folder> folder = folderRepo.findById(folderId);
        if (folder.isPresent()) {
            if (authorizationCheck.checkAuthorization(folder.get().getUser().getId())) {
                List<MaterialResponseDTO> materials = materialService.findMaterialByType(type, folderId);
                return new ResponseEntity<>(materials, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
