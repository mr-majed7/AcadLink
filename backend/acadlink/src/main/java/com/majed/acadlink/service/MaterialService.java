package com.majed.acadlink.service;

import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.entitie.Book;
import com.majed.acadlink.entitie.LectureNote;
import com.majed.acadlink.entitie.LectureSlide;
import com.majed.acadlink.entitie.Other;
import com.majed.acadlink.repository.*;
import com.majed.acadlink.utility.SaveMaterialUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class MaterialService {
    @Autowired
    private FolderRepo folderRepo;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private LectureSlideRepo lectureSlideRepo;
    @Autowired
    private LectureNoteRepo lectureNoteRepo;
    @Autowired
    private OtherRepo otherRepo;

    public MaterialResponseDTO saveMaterial(MaterialAddDTO materialData) {
        SaveMaterialUtil saveMaterialUtil = new SaveMaterialUtil();
        try {
            if (materialData.getFile() != null && !materialData.getFile().isEmpty()) {
                return saveMaterialUtil.saveMaterialFile(materialData);
            } else if (materialData.getLink() != null && !materialData.getLink().isEmpty()) {
                return saveMaterialUtil.saveMaterialLink(materialData);
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }
    }

    public MaterialResponseDTO findMaterial(String type, UUID id) {
        switch (type) {
            case "book":
                Optional<Book> book = bookRepo.findById(id);
                return book.map(value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(), value.getFolder().getId())).orElse(null);
            case "lecture-slide":
                Optional<LectureSlide> lectureSlide = lectureSlideRepo.findById(id);
                return lectureSlide.map(value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(), value.getFolder().getId())).orElse(null);
            case "lecture-note":
                Optional<LectureNote> lectureNote = lectureNoteRepo.findById(id);
                return lectureNote.map(value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(), value.getFolder().getId())).orElse(null);
            case "other":
                Optional<Other> other = otherRepo.findById(id);
                return other.map(value -> new MaterialResponseDTO(value.getId(), value.getName(), value.getLink(), value.getFolder().getId())).orElse(null);
            default:
                return null;
        }
    }

}

