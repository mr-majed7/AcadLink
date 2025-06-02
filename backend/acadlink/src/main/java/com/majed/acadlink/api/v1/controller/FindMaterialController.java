package com.majed.acadlink.api.v1.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.service.FindMaterialsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "6. Find Materials", description = "Endpoints for finding materials")
public class FindMaterialController {
    private final FindMaterialsService findMaterialsService;

    public FindMaterialController(FindMaterialsService findMaterialsService) {
        this.findMaterialsService = findMaterialsService;
    }

    @Operation(summary = "Search Materials with keywords", tags = "6. Find Materials")
    @GetMapping(value = "/search-materials")
    public ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> searchMaterials(
            @RequestParam String keyWords
    ) {
        return findMaterialsService.searchMaterials(keyWords);
    }

    @Operation(summary = "View Materilas of Peers", tags = "6. Find Materials")
    @GetMapping(value = "/view-peers-materials/{peers-user-id}")
    public ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> viewPeersMaterials(
            @PathVariable("peers-user-id") UUID peersUserId
    ) {
        return findMaterialsService.findPeerMaterials(peersUserId);
    }
}
