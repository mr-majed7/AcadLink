package com.majed.acadlink.service;

import com.majed.acadlink.dto.folder.AllFolderResponseDTO;
import com.majed.acadlink.dto.folder.FolderCreateDTO;
import com.majed.acadlink.dto.folder.FolderResponseDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.entitie.Folder;
import com.majed.acadlink.entitie.User;
import com.majed.acadlink.repository.*;
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
    private BookRepo bookRepo;
    @Autowired
    private LectureSlideRepo lectureSlideRepo;
    @Autowired
    private LectureNoteRepo lectureNoteRepo;
    @Autowired
    private OtherRepo otherRepo;
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
        List<MaterialResponseDTO> books = folder.getBooks().stream()
                .map(book -> new MaterialResponseDTO(book.getId(), book.getName(), book.getLink(), folder.getId()))
                .collect(Collectors.toList());

        List<MaterialResponseDTO> lectureSlides = folder.getLectureSlides().stream()
                .map(lectureSlide -> new MaterialResponseDTO(lectureSlide.getId(), lectureSlide.getName(),
                        lectureSlide.getLink(), folder.getId()))
                .collect(Collectors.toList());
        List<MaterialResponseDTO> lectureNotes = folder.getLectureNotes().stream()
                .map(lectureNote -> new MaterialResponseDTO(lectureNote.getId(), lectureNote.getName()
                        , lectureNote.getLink(), folder.getId()))
                .collect(Collectors.toList());
        List<MaterialResponseDTO> others = folder.getOthers().stream()
                .map(other -> new MaterialResponseDTO(other.getId(), other.getName(), other.getLink(), folder.getId()))
                .collect(Collectors.toList());
        return new FolderResponseDTO(folder.getId(), folder.getName(), folder.getCreatedAt(),
                books, lectureSlides, lectureNotes, others);
    }
}
