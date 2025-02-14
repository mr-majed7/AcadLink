package com.majed.acadlink.service;

import com.majed.acadlink.domain.entitie.Folder;
import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.FolderRepo;
import com.majed.acadlink.dto.folder.AllFolderResponseDTO;
import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderResponseDTO;
import com.majed.acadlink.dto.folder.UpdateFolderResponseDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.utility.GetUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FolderService {
    private final FolderRepo folderRepo;
    private final GetUserUtil getUserUtil;

    public FolderService(FolderRepo folderRepo, GetUserUtil getUserUtil) {
        this.folderRepo = folderRepo;
        this.getUserUtil = getUserUtil;
    }


    public AllFolderResponseDTO addFolder(FolderCreateDTO folderData) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();
        if (user.isPresent()) {
            Folder folder = new Folder();
            folder.setName(folderData.getName());
            folder.setPrivacy(folderData.getPrivacy());
            folder.setUser(user.get());
            Optional<Folder> addedFolder = Optional.of(folderRepo.save(folder));
            return new AllFolderResponseDTO(addedFolder.get().getId(), addedFolder.get().getName(),
                    addedFolder.get().getCreatedAt(), addedFolder.get().getPrivacy());
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
                    .map(folder -> new AllFolderResponseDTO(folder.getId(), folder.getName(),
                            folder.getCreatedAt(), folder.getPrivacy()))
                    .toList();
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public FolderResponseDTO getFolder(Folder folder) {
        List<MaterialResponseDTO> materials = folder.getMaterials().stream()
                .map(material -> new MaterialResponseDTO(material.getId(), material.getName(), material.getLink(),
                        material.getType(), material.getPrivacy(), folder.getId()))
                .toList();

        return new FolderResponseDTO(folder.getId(), folder.getName(), folder.getCreatedAt(), folder.getPrivacy(), materials);
    }

    public UpdateFolderResponseDTO updateFolder(Folder folder, FolderCreateDTO newData) {
        if (newData.getName() != null) {
            folder.setName(newData.getName());
        }
        if (newData.getPrivacy() != null) {
            folder.setPrivacy(newData.getPrivacy());
        }

        Folder updatedFolder = folderRepo.save(folder);

        return new UpdateFolderResponseDTO(updatedFolder.getId(), updatedFolder.getName()
                , updatedFolder.getCreatedAt(), updatedFolder.getPrivacy()
        );
    }
}
