package com.majed.acadlink.controller;

import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderDTO;
import com.majed.acadlink.service.FolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/folder")
@Slf4j
public class FolderController {
    @Autowired
    private FolderService folderService;

    @PostMapping("/create")
    public ResponseEntity<FolderDTO> createFolder(@RequestBody FolderCreateDTO folderData) {
        try {
            FolderDTO addedFolder = folderService.addFolder(folderData);
            return new ResponseEntity<>(addedFolder, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<FolderDTO>> getAllFolders() {
        List<FolderDTO> folders = folderService.getAllFolders();

        if (folders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(folders, HttpStatus.OK);
        }
    }

}
