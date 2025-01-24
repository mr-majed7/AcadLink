package com.majed.acadlink.controller;

import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.repository.FolderRepo;
import com.majed.acadlink.service.MaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/material")
@Slf4j
public class MaterialsController {
    @Autowired
    private FolderRepo folderRepo;
    @Autowired
    private MaterialService materialService;

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

    @GetMapping("/get-material/{id}")
    public ResponseEntity<MaterialResponseDTO> getMaterial(@PathVariable UUID id) {
        MaterialResponseDTO response = materialService.findMaterial(id);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
