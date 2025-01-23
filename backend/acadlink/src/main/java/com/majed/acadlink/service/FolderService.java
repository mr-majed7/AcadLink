package com.majed.acadlink.service;

import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderDTO;
import com.majed.acadlink.entitie.Folder;
import com.majed.acadlink.entitie.User;
import com.majed.acadlink.repository.FolderRepo;
import com.majed.acadlink.utility.GetUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class FolderService {
    @Autowired
    private FolderRepo folderRepo;

    @Autowired
    private GetUserUtil getUserUtil;


    public FolderDTO addFolder(FolderCreateDTO folderData) {
        Optional<User> user = getUserUtil.getAuthenticatedUser();
        if (user.isPresent()) {
            Folder folder = new Folder();
            folder.setName(folderData.getName());
            folder.setUser(user.get());
            Optional<Folder> addedFolder = Optional.of(folderRepo.save(folder));
            return new FolderDTO(addedFolder.get().getId(), addedFolder.get().getName());
        } else {
            log.error("User does not exists");
            return null;
        }
    }
}
