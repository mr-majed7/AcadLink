package com.majed.acadlink.api.v1.controller;


import com.majed.acadlink.domain.entitie.Folder;
import com.majed.acadlink.domain.entitie.Materials;
import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.service.MaterialService;
import com.majed.acadlink.utility.AuthorizationCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/material")
@Slf4j
@Tag(name = "4. Materials Management", description = "Endpoints for managing materials")
public class MaterialsController {
    @Autowired
    private com.majed.acadlink.repository.FolderRepo folderRepo;
    @Autowired
    private MaterialService materialService;
    @Autowired
    private AuthorizationCheck authorizationCheck;
    @Autowired
    private com.majed.acadlink.repository.MaterialsRepo materialsRepo;

    @Operation(summary = "Add a new material", tags = {"4. Materials Management"})
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

    @Operation(summary = "Get material by ID", tags = {"4. Materials Management"})
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

    @Operation(summary = "Get materials by type and folder ID", tags = {"4. Materials Management"})
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

    @Operation(summary = "Update material by ID", tags = {"4. Materials Management"})
    @PutMapping(value = "/update-material/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MaterialResponseDTO> updateMaterial(@PathVariable UUID id, @ModelAttribute MaterialAddDTO newData) throws IOException {
        Optional<Materials> current = materialsRepo.findById(id);

        if (current.isPresent()) {
            Folder folder = folderRepo.findById(current.get().getFolder().getId()).get();
            if (authorizationCheck.checkAuthorization(folder.getUser().getId())) {
                MaterialResponseDTO response = materialService.updateMaterial(current.get(), newData);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete material by ID", tags = {"4. Materials Management"})
    @DeleteMapping("/delete-material/{id}")
    public ResponseEntity<Boolean> deleteMaterial(@PathVariable UUID id) {
        Optional<Materials> material = materialsRepo.findById(id);

        if (material.isPresent()) {
            Folder folder = folderRepo.findById(material.get().getFolder().getId()).get();
            if (authorizationCheck.checkAuthorization(folder.getUser().getId())) {
                materialsRepo.delete(material.get());
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
