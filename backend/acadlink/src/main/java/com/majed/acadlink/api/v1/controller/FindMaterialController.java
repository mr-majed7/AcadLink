package com.majed.acadlink.api.v1.controller;

import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.service.FindMaterialsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "5. Find Materials", description = "Endpoints for finding materials")
public class FindMaterialController {
    private final FindMaterialsService findMaterials;

    public FindMaterialController(FindMaterialsService findMaterials) {
        this.findMaterials = findMaterials;
    }

    @Operation(summary = "Search Materials with keywords", tags = "5. Find Materials")
    @GetMapping(value = "/search-materials")
    public ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> searchMaterials(
            @RequestParam String keyWords
    ) {
        return findMaterials.searchMaterials(keyWords);
    }
}
