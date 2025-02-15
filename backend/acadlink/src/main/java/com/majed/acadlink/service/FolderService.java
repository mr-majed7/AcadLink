package com.majed.acadlink.service;

import com.majed.acadlink.domain.entitie.Folder;
import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.FolderRepo;
import com.majed.acadlink.dto.ErrorResponseDTO;
import com.majed.acadlink.dto.folder.AllFolderResponseDTO;
import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderResponseDTO;
import com.majed.acadlink.dto.folder.UpdateFolderResponseDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.utility.AuthorizationCheck;
import com.majed.acadlink.utility.GetUserUtil;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing folders.
 */
@Service
@Slf4j
public class FolderService {
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
    public ResponseEntity<Either<ErrorResponseDTO, AllFolderResponseDTO>> addFolder(FolderCreateDTO folderData) {
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
            return new ResponseEntity<>(Either.right(response), HttpStatus.OK);
        } else {
            log.error("User does not exists");
            return new ResponseEntity<>(
                    Either.left(new ErrorResponseDTO("User Not Found", HttpStatus.BAD_REQUEST.value())),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves all folders for the authenticated user.
     *
     * @return the response entity containing the list of all folders or an error status
     */
    public ResponseEntity<Either<ErrorResponseDTO, List<AllFolderResponseDTO>>> getAllFolders() {
        Optional<User> user = getUserUtil.getAuthenticatedUser();

        if (user.isPresent()) {
            List<Folder> folders = folderRepo.findByUserId(user.get().getId());
            List<AllFolderResponseDTO> folderList = folders.stream()
                    .map(folder -> new AllFolderResponseDTO(folder.getId(), folder.getName(),
                            folder.getCreatedAt(), folder.getPrivacy()))
                    .toList();
            return new ResponseEntity<>(Either.right(folderList), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    Either.left(new ErrorResponseDTO("User Not Found", HttpStatus.BAD_REQUEST.value())),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves a specific folder by ID.
     *
     * @param folderId the ID of the folder to retrieve
     * @return the response entity containing the folder or an error status
     */
    public ResponseEntity<Either<ErrorResponseDTO, FolderResponseDTO>> getFolder(UUID folderId) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();
        if (user.isEmpty()) {
            return new ResponseEntity<>(
                    Either.left(new ErrorResponseDTO("User Not Found", HttpStatus.BAD_GATEWAY.value())),
                    HttpStatus.BAD_REQUEST);
        } else {
            if (!authorizationCheck.checkAuthorization(user.get().getId())) {
                return new ResponseEntity<>(
                        Either.left(new ErrorResponseDTO("Not Authorized", HttpStatus.FORBIDDEN.value()))
                        , HttpStatus.FORBIDDEN);
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
                    return new ResponseEntity<>(Either.right(folderResponse), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(
                            Either.left(new ErrorResponseDTO("Folder Not Found", HttpStatus.NOT_FOUND.value())),
                            HttpStatus.NOT_FOUND);
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
    public ResponseEntity<Either<ErrorResponseDTO, UpdateFolderResponseDTO>> updateFolder(
            UUID folderId,
            FolderCreateDTO newData) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();
        if (user.isEmpty()) {
            return new ResponseEntity<>(
                    Either.left(new ErrorResponseDTO("User Not Found", HttpStatus.BAD_REQUEST.value())),
                    HttpStatus.BAD_REQUEST);
        }

        if (!authorizationCheck.checkAuthorization(user.get().getId())) {
            return new ResponseEntity<>(
                    Either.left(new ErrorResponseDTO("Not Authorized", HttpStatus.FORBIDDEN.value())),
                    HttpStatus.FORBIDDEN);
        }

        return folderRepo.findById(folderId)
                .map(folder -> {
                    Either<ErrorResponseDTO, UpdateFolderResponseDTO> result = updateFolderData(folder, newData);
                    return new ResponseEntity<>(result, result.isRight() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
                })
                .orElseGet(() -> new ResponseEntity<>(
                        Either.left(new ErrorResponseDTO("Folder Not Found",
                                HttpStatus.NOT_FOUND.value())), HttpStatus.NOT_FOUND));
    }

    /**
     * Updates the folder data with the new data provided.
     *
     * @param folder  the folder to be updated
     * @param newData the new data for the folder
     * @return the updated folder data
     */
    private Either<ErrorResponseDTO, UpdateFolderResponseDTO> updateFolderData(Folder folder, FolderCreateDTO newData) {
        if (newData.getName() != null) {
            folder.setName(newData.getName());
        }
        if (newData.getPrivacy() != null) {
            folder.setPrivacy(newData.getPrivacy());
        }

        Folder updatedFolder = folderRepo.save(folder);
        UpdateFolderResponseDTO response = new UpdateFolderResponseDTO(updatedFolder.getId(),
                updatedFolder.getName(), updatedFolder.getCreatedAt(), updatedFolder.getPrivacy());
        return Either.right(response);
    }
}
