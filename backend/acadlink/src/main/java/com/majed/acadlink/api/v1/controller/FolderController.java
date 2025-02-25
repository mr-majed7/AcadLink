package com.majed.acadlink.api.v1.controller;

import com.majed.acadlink.dto.ApiResponse;
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

/**
 * Controller for managing folders.
 */
@Tag(name = "3. Folder Management", description = "Endpoints for managing folders")
@RestController
@RequestMapping("/folder")
@Slf4j
public class FolderController {
    private final FolderService folderService;

    /**
     * Constructor for FolderController.
     *
     * @param folderService the folder service
     */
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    /**
     * Creates a new folder.
     *
     * @param folderData the data for the new folder
     * @return the response entity containing the added folder or an error status
     */
    @Operation(summary = "Create a new folder", tags = {"3. Folder Management"})
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AllFolderResponseDTO>> createFolder(@RequestBody FolderCreateDTO folderData) {
        return folderService.addFolder(folderData);
    }

    /**
     * Retrieves all folders.
     *
     * @return the response entity containing the list of all folders or an error status
     */
    @Operation(summary = "Get all folders", tags = {"3. Folder Management"})
    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<AllFolderResponseDTO>>> getAllFolders() {
        return folderService.getAllFolders();
    }

    /**
     * Retrieves a specific folder by ID.
     *
     * @param folderId the ID of the folder to retrieve
     * @return the response entity containing the folder or an error status
     */
    @Operation(summary = "Get a specific folder by ID", tags = {"3. Folder Management"})
    @GetMapping("/get-folder/{folderId}")
    public ResponseEntity<ApiResponse<FolderResponseDTO>> getFolder(@PathVariable UUID folderId) {
        return folderService.getFolder(folderId);
    }

    /**
     * Updates a specific folder by ID.
     *
     * @param folderId the ID of the folder to update
     * @param newData  the new data for the folder
     * @return the response entity containing the updated folder or an error status
     */
    @Operation(summary = "Update a specific folder by ID", tags = {"3. Folder Management"})
    @PutMapping("/update-folder/{folderId}")
    public ResponseEntity<ApiResponse<UpdateFolderResponseDTO>> updateFolder(@PathVariable UUID folderId,
                                                                             @RequestBody FolderCreateDTO newData) {
        return folderService.updateFolder(folderId, newData);
    }
    //WORK ON DELETE FOLDER AFTER FINISHING MATERIAL UPDATE AND DELETE

}
