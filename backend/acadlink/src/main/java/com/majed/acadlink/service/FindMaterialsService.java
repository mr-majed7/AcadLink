package com.majed.acadlink.service;

import com.majed.acadlink.domain.entity.Peers;
import com.majed.acadlink.domain.entity.User;
import com.majed.acadlink.domain.repository.MaterialsRepo;
import com.majed.acadlink.domain.repository.PeersRepo;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.enums.PeerStatus;
import com.majed.acadlink.enums.Privacy;
import com.majed.acadlink.utility.GetUserUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FindMaterialsService {
    private final UserRepo userRepo;
    private final MaterialsRepo materialsRepo;
    private final PeersRepo peersRepo;
    private final GetUserUtil getUserUtil;

    public FindMaterialsService(
            UserRepo userRepo,
            MaterialsRepo materialsRepo,
            PeersRepo peersRepo,
            GetUserUtil getUserUtil
    ) {
        this.userRepo = userRepo;
        this.materialsRepo = materialsRepo;
        this.peersRepo = peersRepo;
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

    public ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> findPeerMaterials(UUID peerUserId) {
        Optional<User> currentUser = getUserUtil.getAuthenticatedUser();

        if (currentUser.isEmpty()) {
            return ApiResponse.error("Not logged in", HttpStatus.FORBIDDEN);
        }

        Optional<User> peerUser = userRepo.findById(peerUserId);

        if (peerUser.isEmpty()) {
            return ApiResponse.error("No user exists with this id", HttpStatus.NOT_FOUND);
        }

        Peers peer = peersRepo.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(currentUser.get().getId(),
                peerUserId, currentUser.get().getId(), peerUserId);

        if (peer == null || peer.getStatus() != PeerStatus.ACCEPTED) {
            return ApiResponse.error("Users are not peers", HttpStatus.BAD_REQUEST);
        }

        List<MaterialResponseDTO> materials = materialsRepo.findAllByUserIdAndPrivacy(
                        peerUserId, currentUser.get().getInstitute()).stream()
                .map(value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(),
                        value.getType(), value.getPrivacy(), value.getFolder().getId()))
                .toList();

        return ApiResponse.success(materials, HttpStatus.OK);
    }


}
