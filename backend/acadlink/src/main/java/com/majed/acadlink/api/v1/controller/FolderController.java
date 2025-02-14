package com.majed.acadlink.api.v1.controller;

import com.majed.acadlink.dto.folder.AllFolderResponseDTO;
import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderResponseDTO;
import com.majed.acadlink.dto.folder.UpdateFolderResponseDTO;
import com.majed.acadlink.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "3. Folder Management", description = "Endpoints for managing folders")
@RestController
@RequestMapping("/folder")
@Slf4j
public class FolderController {
    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @Operation(summary = "Create a new folder", tags = {"3. Folder Management"})
    @PostMapping("/create")
    public ResponseEntity<AllFolderResponseDTO> createFolder(@RequestBody FolderCreateDTO folderData) {
        return folderService.addFolder(folderData);
    }


    @Operation(summary = "Get all folders", tags = {"3. Folder Management"})
    @GetMapping("/get-all")
    public ResponseEntity<List<AllFolderResponseDTO>> getAllFolders() {
        return folderService.getAllFolders();
    }

    @Operation(summary = "Get a specific folder by ID", tags = {"3. Folder Management"})
    @GetMapping("/get-folder/{folderId}")
    public ResponseEntity<FolderResponseDTO> getFolder(@PathVariable UUID folderId) {
        return folderService.getFolder(folderId);
    }

    @Operation(summary = "Update a specific folder by ID", tags = {"3. Folder Management"})
    @PutMapping("/update-folder/{folderId}")
    public ResponseEntity<UpdateFolderResponseDTO> updateFolder(@PathVariable UUID folderId, @RequestBody FolderCreateDTO newData) {
        return folderService.updateFolder(folderId, newData);
    }
    //WORK ON DELETE FOLDER AFTER FINISHING MATERIAL UPDATE AND DELETE

}
