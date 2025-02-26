package com.majed.acadlink.service;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.FolderRepo;
import com.majed.acadlink.domain.repository.MaterialsRepo;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.Privacy;
import com.majed.acadlink.utility.GetUserUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FindMaterialsService {
    private final FolderRepo folderRepo;
    private final MaterialsRepo materialsRepo;
    private final GetUserUtil getUserUtil;

    public FindMaterialsService(
            FolderRepo folderRepo,
            MaterialsRepo materialsRepo,
            GetUserUtil getUserUtil
    ) {
        this.folderRepo = folderRepo;
        this.materialsRepo = materialsRepo;
        this.getUserUtil = getUserUtil;
    }


    public ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> searchMaterials(
            String keyWords
    ) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();

        if (user.isEmpty()) {
            return ApiResponse.error("User not found", HttpStatus.BAD_REQUEST);
        }

        List<MaterialResponseDTO> materials = new ArrayList<>();

        materials.addAll(
                materialsRepo.searchPublicMaterials(keyWords, Privacy.PUBLIC).stream().map(
                        value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(),
                                value.getType(), value.getPrivacy(), value.getFolder().getId())).toList());


        materials.addAll(
                materialsRepo.searchPeerMaterials(keyWords, user.get().getId(), Privacy.PEERS).stream().map(
                        value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(),
                                value.getType(), value.getPrivacy(), value.getFolder().getId())).toList());


        materials.addAll(
                materialsRepo.searchInstitutionalMaterials(keyWords, user.get().getInstitute(), Privacy.INSTITUTIONAL).
                        stream().map(
                                value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(),
                                        value.getType(), value.getPrivacy(), value.getFolder().getId())).toList());


        return ApiResponse.success(materials, HttpStatus.OK);
    }
}
