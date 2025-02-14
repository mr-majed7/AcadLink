package com.majed.acadlink.service;

import com.majed.acadlink.domain.entitie.Folder;
import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.FolderRepo;
import com.majed.acadlink.dto.folder.AllFolderResponseDTO;
import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderResponseDTO;
import com.majed.acadlink.dto.folder.UpdateFolderResponseDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.utility.AuthorizationCheck;
import com.majed.acadlink.utility.GetUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class FolderService {
    private final FolderRepo folderRepo;
    private final GetUserUtil getUserUtil;
    private final AuthorizationCheck authorizationCheck;

    public FolderService(FolderRepo folderRepo, GetUserUtil getUserUtil, AuthorizationCheck authorizationCheck) {
        this.folderRepo = folderRepo;
        this.getUserUtil = getUserUtil;
        this.authorizationCheck = authorizationCheck;
    }


    public ResponseEntity<AllFolderResponseDTO> addFolder(FolderCreateDTO folderData) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();
        if (user.isPresent()) {
            Folder folder = new Folder();
            folder.setName(folderData.getName());
            folder.setPrivacy(folderData.getPrivacy());
            folder.setUser(user.get());
            Optional<Folder> addedFolder = Optional.of(folderRepo.save(folder));
            AllFolderResponseDTO response = new AllFolderResponseDTO(addedFolder.get().getId(), addedFolder.get().getName(),
                    addedFolder.get().getCreatedAt(), addedFolder.get().getPrivacy());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            log.error("User does not exists");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<AllFolderResponseDTO>> getAllFolders() {
        Optional<User> user = getUserUtil.getAuthenticatedUser();

        if (user.isPresent()) {
            List<Folder> folders = folderRepo.findByUserId(user.get().getId());
            List<AllFolderResponseDTO> folderList = folders.stream()
                    .map(folder -> new AllFolderResponseDTO(folder.getId(), folder.getName(),
                            folder.getCreatedAt(), folder.getPrivacy()))
                    .toList();
            return new ResponseEntity<>(folderList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<FolderResponseDTO> getFolder(UUID folderId) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            if (!authorizationCheck.checkAuthorization(user.get().getId())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else {
                Optional<Folder> findFolder = folderRepo.findById(folderId);
                if (findFolder.isPresent()) {
                    Folder folder = findFolder.get();
                    List<MaterialResponseDTO> materials = folder.getMaterials().stream()
                            .map(material -> new MaterialResponseDTO(material.getId(), material.getName(), material.getLink(),
                                    material.getType(), material.getPrivacy(), folder.getId()))
                            .toList();

                    FolderResponseDTO folderResponse = new FolderResponseDTO(folder.getId(), folder.getName(),
                            folder.getCreatedAt(), folder.getPrivacy(), materials);
                    return new ResponseEntity<>(folderResponse, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
        }
    }

    public ResponseEntity<UpdateFolderResponseDTO> updateFolder(UUID folderId, FolderCreateDTO newData) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!authorizationCheck.checkAuthorization(user.get().getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return folderRepo.findById(folderId)
                .map(folder -> updateFolderData(folder, newData))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<UpdateFolderResponseDTO> updateFolderData(Folder folder, FolderCreateDTO newData) {
        if (newData.getName() != null) {
            folder.setName(newData.getName());
        }
        if (newData.getPrivacy() != null) {
            folder.setPrivacy(newData.getPrivacy());
        }

        Folder updatedFolder = folderRepo.save(folder);
        UpdateFolderResponseDTO response = new UpdateFolderResponseDTO(updatedFolder.getId(),
                updatedFolder.getName(), updatedFolder.getCreatedAt(), updatedFolder.getPrivacy());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
