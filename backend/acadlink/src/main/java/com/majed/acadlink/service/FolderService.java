package com.majed.acadlink.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.majed.acadlink.domain.entitie.Folder;
import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.FolderRepo;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.folder.AllFolderResponseDTO;
import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderResponseDTO;
import com.majed.acadlink.dto.folder.UpdateFolderResponseDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.utility.AuthorizationCheck;
import com.majed.acadlink.utility.GetUserUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing folders.
 */
@Service
@Slf4j
public class FolderService {
    private static final String NO_USER_FOUND_MESSAGE = "No user found";
    
    private final FolderRepo folderRepo;
    private final GetUserUtil getUserUtil;
    private final AuthorizationCheck authorizationCheck;

    /**
     * Constructor for FolderService.
     *
     * @param folderRepo         the folder repository
     * @param getUserUtil        utility to get the authenticated user
     * @param authorizationCheck utility to check user authorization
     */
    public FolderService(FolderRepo folderRepo, GetUserUtil getUserUtil, AuthorizationCheck authorizationCheck) {
        this.folderRepo = folderRepo;
        this.getUserUtil = getUserUtil;
        this.authorizationCheck = authorizationCheck;
    }


    /**
     * Adds a new folder.
     *
     * @param folderData the data for the new folder
     * @return the response entity containing the added folder or an error status
     */
    public ResponseEntity<ApiResponse<AllFolderResponseDTO>> addFolder(FolderCreateDTO folderData) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();
        if (user.isPresent()) {
            Folder folder = new Folder();
            folder.setName(folderData.getName());
            folder.setPrivacy(folderData.getPrivacy());
            folder.setUser(user.get());
            Optional<Folder> addedFolder = Optional.of(folderRepo.save(folder));
            AllFolderResponseDTO response = new AllFolderResponseDTO(
                    addedFolder.get().getId(), addedFolder.get().getName(),
                    addedFolder.get().getCreatedAt(), addedFolder.get().getPrivacy());
            return ApiResponse.success(response, HttpStatus.CREATED);
        } else {
            log.error("User does not exists");
            return ApiResponse.error(NO_USER_FOUND_MESSAGE, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves all folders for the authenticated user.
     *
     * @return the response entity containing the list of all folders or an error status
     */
    public ResponseEntity<ApiResponse<List<AllFolderResponseDTO>>> getAllFolders() {
        Optional<User> user = getUserUtil.getAuthenticatedUser();

        if (user.isPresent()) {
            List<Folder> folders = folderRepo.findByUserId(user.get().getId());
            List<AllFolderResponseDTO> folderList = folders.stream()
                    .map(folder -> new AllFolderResponseDTO(folder.getId(), folder.getName(),
                            folder.getCreatedAt(), folder.getPrivacy()))
                    .toList();
            return ApiResponse.success(folderList, HttpStatus.OK);
        } else {
            return ApiResponse.error(NO_USER_FOUND_MESSAGE, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves a specific folder by ID.
     *
     * @param folderId the ID of the folder to retrieve
     * @return the response entity containing the folder or an error status
     */
    public ResponseEntity<ApiResponse<FolderResponseDTO>> getFolder(UUID folderId) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();
        if (user.isEmpty()) {
            return ApiResponse.error(NO_USER_FOUND_MESSAGE, HttpStatus.BAD_REQUEST);
        } else {
            if (!authorizationCheck.checkAuthorization(user.get().getId())) {
                return ApiResponse.error("Not Authorized", HttpStatus.FORBIDDEN);
            } else {
                Optional<Folder> findFolder = folderRepo.findById(folderId);
                if (findFolder.isPresent()) {
                    Folder folder = findFolder.get();
                    List<MaterialResponseDTO> materials = folder.getMaterials().stream().map(
                                    material -> new MaterialResponseDTO(material.getId(), material.getName(),
                                            material.getLink(), material.getType(), material.getPrivacy(), folder.getId()))
                            .toList();

                    FolderResponseDTO folderResponse = new FolderResponseDTO(folder.getId(), folder.getName(),
                            folder.getCreatedAt(), folder.getPrivacy(), materials);
                    return ApiResponse.success(folderResponse, HttpStatus.OK);
                } else {
                    return ApiResponse.error("Folder not found", HttpStatus.NOT_FOUND);
                }
            }
        }
    }

    /**
     * Updates a specific folder by ID.
     *
     * @param folderId the ID of the folder to update
     * @param newData  the new data for the folder
     * @return the response entity containing the updated folder or an error status
     */
    public ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> updateFolder(
            UUID folderId,
            FolderCreateDTO newData) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();
        if (user.isEmpty()) {
            return ApiResponse.error(NO_USER_FOUND_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        if (!authorizationCheck.checkAuthorization(user.get().getId())) {
            return ApiResponse.error("Not authorized", HttpStatus.FORBIDDEN);
        }

        return folderRepo.findById(folderId)
                .map(folder -> {
                    UpdateFolderResponseDTO result = updateFolderData(folder, newData);
                    return ApiResponse.success(result, HttpStatus.OK);
                })
                .orElseGet(() -> ApiResponse.error("No folder found", HttpStatus.BAD_REQUEST));
    }

    /**
     * Updates the folder data with the new data provided.
     *
     * @param folder  the folder to be updated
     * @param newData the new data for the folder
     * @return the updated folder data
     */
    private UpdateFolderResponseDTO updateFolderData(Folder folder, FolderCreateDTO newData) {
        if (newData.getName() != null) {
            folder.setName(newData.getName());
        }
        if (newData.getPrivacy() != null) {
            folder.setPrivacy(newData.getPrivacy());
        }

        Folder updatedFolder = folderRepo.save(folder);
        return new UpdateFolderResponseDTO(
                updatedFolder.getId(),
                updatedFolder.getName(),
                updatedFolder.getCreatedAt(),
                updatedFolder.getPrivacy()
        );
    }
}
