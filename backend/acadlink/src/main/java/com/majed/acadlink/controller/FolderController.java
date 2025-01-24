package com.majed.acadlink.controller;

import com.majed.acadlink.dto.folder.AllFolderResponseDTO;
import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderResponseDTO;
import com.majed.acadlink.entitie.Folder;
import com.majed.acadlink.repository.FolderRepo;
import com.majed.acadlink.service.FolderService;
import com.majed.acadlink.utility.AuthorizationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/folder")
@Slf4j
public class FolderController {
    @Autowired
    private FolderRepo folderRepo;
    @Autowired
    private FolderService folderService;
    @Autowired
    private AuthorizationCheck authorizationCheck;

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

    @GetMapping("/get-all")
    public ResponseEntity<List<AllFolderResponseDTO>> getAllFolders() {
        List<AllFolderResponseDTO> folders = folderService.getAllFolders();

        if (folders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(folders, HttpStatus.OK);
        }
    }

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

}
