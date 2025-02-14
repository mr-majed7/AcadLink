package com.majed.acadlink.api.v1.controller;

import com.majed.acadlink.domain.entitie.Folder;
import com.majed.acadlink.dto.folder.AllFolderResponseDTO;
import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderResponseDTO;
import com.majed.acadlink.dto.folder.UpdateFolderResponseDTO;
import com.majed.acadlink.service.FolderService;
import com.majed.acadlink.utility.AuthorizationCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "3. Folder Management", description = "Endpoints for managing folders")
@RestController
@RequestMapping("/folder")
@Slf4j
public class FolderController {
    @Autowired
    private com.majed.acadlink.repository.FolderRepo folderRepo;
    @Autowired
    private FolderService folderService;
    @Autowired
    private AuthorizationCheck authorizationCheck;

    @Operation(summary = "Create a new folder", tags = {"3. Folder Management"})
    @PostMapping("/create")
    public ResponseEntity<AllFolderResponseDTO> createFolder(@RequestBody FolderCreateDTO folderData) {
        try {
            AllFolderResponseDTO addedFolder = folderService.addFolder(folderData);
            return new ResponseEntity<>(addedFolder, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get all folders", tags = {"3. Folder Management"})
    @GetMapping("/get-all")
    public ResponseEntity<List<AllFolderResponseDTO>> getAllFolders() {
        List<AllFolderResponseDTO> folders = folderService.getAllFolders();

        if (folders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(folders, HttpStatus.OK);
        }
    }

    @Operation(summary = "Get a specific folder by ID", tags = {"3. Folder Management"})
    @GetMapping("/get-folder/{folderId}")
    public ResponseEntity<FolderResponseDTO> getFolder(@PathVariable UUID folderId) {
        Optional<Folder> folder = folderRepo.findById(folderId);

        if (folder.isPresent()) {
            if (authorizationCheck.checkAuthorization(folder.get().getUser().getId())) {
                FolderResponseDTO response = folderService.getFolder(folder.get());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update a specific folder by ID", tags = {"3. Folder Management"})
    @PutMapping("/update-folder/{folderId}")
    public ResponseEntity<UpdateFolderResponseDTO> updateFolder(@PathVariable UUID folderId, @RequestBody FolderCreateDTO newData) {
        Optional<Folder> folder = folderRepo.findById(folderId);

        if (folder.isPresent()) {
            if (authorizationCheck.checkAuthorization(folder.get().getUser().getId())) {
                UpdateFolderResponseDTO response = folderService.updateFolder(folder.get(), newData);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //WORK ON DELETE FOLDER AFTER FINISHING MATERIAL UPDATE AND DELETE

}
