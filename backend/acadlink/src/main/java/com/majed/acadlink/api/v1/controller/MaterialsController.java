package com.majed.acadlink.api.v1.controller;

import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Controller for managing materials.
 */
@RestController
@RequestMapping("/material")
@Slf4j
@Tag(name = "4. Materials Management", description = "Endpoints for managing materials")
public class MaterialsController {
    private final MaterialService materialService;

    /**
     * Constructor for MaterialsController.
     */
    public MaterialsController(MaterialService materialService) {
        this.materialService = materialService;
    }

    /**
     * Adds a new material.
     *
     * @param materialData the data for the new material
     * @return the response entity containing the added material or an error status
     */
    @Operation(summary = "Add a new material", tags = {"4. Materials Management"})
    @PostMapping(value = "/add-material", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<MaterialResponseDTO>> addMaterials(
            @ModelAttribute MaterialAddDTO materialData) {
        return materialService.saveMaterial(materialData);
    }

    /**
     * Retrieves a material by ID.
     *
     * @param id the ID of the material to retrieve
     * @return the response entity containing the material or an error status
     */
    @Operation(summary = "Get material by ID", tags = {"4. Materials Management"})
    @GetMapping("/get-material-by-id/{id}")
    public ResponseEntity<ApiResponse<MaterialResponseDTO>> getMaterial(@PathVariable UUID id) {
        return materialService.findMaterial(id);
    }

    /**
     * Retrieves materials by type and folder ID.
     *
     * @param type     the type of the materials to retrieve
     * @param folderId the ID of the folder containing the materials
     * @return the response entity containing the list of materials or an error status
     */
    @Operation(summary = "Get materials by type and folder ID", tags = {"4. Materials Management"})
    @GetMapping("/get-materials-by-type/{type}/{folder-id}")
    public ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> getMaterialByType(
            @PathVariable MaterialType type,
            @PathVariable("folder-id") UUID folderId) {
        return materialService.findMaterialsByType(type, folderId);
    }

    /**
     * Updates a material by ID.
     *
     * @param id      the ID of the material to update
     * @param newData the new data for the material
     * @return the response entity containing the updated material or an error status
     * @throws IOException if an I/O error occurs
     */
    @Operation(summary = "Update material by ID", tags = {"4. Materials Management"})
    @PutMapping(value = "/update-material/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<MaterialResponseDTO>> updateMaterial(
            @PathVariable UUID id,
            @ModelAttribute MaterialAddDTO newData) throws IOException {
        return materialService.updateMaterial(id, newData);
    }

    /**
     * Deletes a material by ID.
     *
     * @param id the ID of the material to delete
     * @return the response entity containing the deletion status
     */
    @Operation(summary = "Delete material by ID", tags = {"4. Materials Management"})
    @DeleteMapping("/delete-material/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteMaterial(@PathVariable UUID id) {
        return materialService.deleteMaterial(id);
    }

}
