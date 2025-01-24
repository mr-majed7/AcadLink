package com.majed.acadlink.service;

import com.majed.acadlink.dto.folder.AllFolderResponseDTO;
import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderResponseDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.entitie.Folder;
import com.majed.acadlink.entitie.User;
import com.majed.acadlink.repository.FolderRepo;
import com.majed.acadlink.utility.GetUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FolderService {
    @Autowired
    private FolderRepo folderRepo;
    @Autowired
    private GetUserUtil getUserUtil;


    public AllFolderResponseDTO addFolder(FolderCreateDTO folderData) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();
        if (user.isPresent()) {
            Folder folder = new Folder();
            folder.setName(folderData.getName());
            folder.setUser(user.get());
            Optional<Folder> addedFolder = Optional.of(folderRepo.save(folder));
            return new AllFolderResponseDTO(addedFolder.get().getId(), addedFolder.get().getName(), addedFolder.get().getCreatedAt());
        } else {
            log.error("User does not exists");
            return null;
        }
    }

    public List<AllFolderResponseDTO> getAllFolders() {
        Optional<User> user = getUserUtil.getAuthenticatedUser();

        if (user.isPresent()) {
            List<Folder> folders = folderRepo.findByUserId(user.get().getId());
            return folders.stream()
                    .map(folder -> new AllFolderResponseDTO(folder.getId(), folder.getName(), folder.getCreatedAt()))
                    .collect(Collectors.toList());
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public FolderResponseDTO getFolder(Folder folder) {
        List<MaterialResponseDTO> materials = folder.getMaterials().stream()
                .map(material -> new MaterialResponseDTO(material.getId(), material.getName(), material.getLink(), material.getType(), folder.getId()))
                .collect(Collectors.toList());

        return new FolderResponseDTO(folder.getId(), folder.getName(), folder.getCreatedAt(), materials);
    }
}
