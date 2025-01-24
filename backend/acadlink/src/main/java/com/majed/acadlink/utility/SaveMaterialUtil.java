package com.majed.acadlink.utility;

import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.entitie.*;
import com.majed.acadlink.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Component
public class SaveMaterialUtil {
    private final String FILE_STORAGE_PATH = "/home/majed/AcadLink/backend/acadlink/storage/materials";
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

    public MaterialResponseDTO saveMaterialFile(MaterialAddDTO materialData) throws IOException {
        MultipartFile file = materialData.getFile();
        String filePath = String.valueOf(Paths.get(FILE_STORAGE_PATH, file.getOriginalFilename()));
        Files.createDirectories(Paths.get(FILE_STORAGE_PATH));
        file.transferTo(new File(filePath));

        return saveMaterialEntity(materialData, filePath);
    }

    public MaterialResponseDTO saveMaterialLink(MaterialAddDTO materialData) {
        String link = materialData.getLink();
        return saveMaterialEntity(materialData, link);
    }

    private MaterialResponseDTO saveMaterialEntity(MaterialAddDTO materialData, String filePath) {
        Optional<Folder> folderOpt = folderRepo.findById(materialData.getFolderId());
        if (!folderOpt.isPresent()) {
            return null;
        }

        Folder folder = folderOpt.get();
        MaterialResponseDTO responseDTO = null;

        switch (materialData.getType()) {
            case "BOOK":
                Book book = new Book();
                book.setFolder(folder);
                book.setName(materialData.getName());
                book.setLink(filePath);
                Book savedBook = bookRepo.save(book);
                responseDTO = new MaterialResponseDTO(
                        savedBook.getId(), savedBook.getName(), savedBook.getLink(), savedBook.getFolder().getId()
                );
                break;

            case "LECTURE_SLIDE":
                LectureSlide lectureSlide = new LectureSlide();
                lectureSlide.setFolder(folder);
                lectureSlide.setName(materialData.getName());
                lectureSlide.setLink(filePath);
                LectureSlide savedLectureSlide = lectureSlideRepo.save(lectureSlide);
                responseDTO = new MaterialResponseDTO(
                        savedLectureSlide.getId(), savedLectureSlide.getName(), savedLectureSlide.getLink(), savedLectureSlide.getFolder().getId()
                );
                break;

            case "LECTURE_NOTE":
                LectureNote lectureNote = new LectureNote();
                lectureNote.setFolder(folder);
                lectureNote.setName(materialData.getName());
                lectureNote.setLink(filePath);
                LectureNote savedLectureNote = lectureNoteRepo.save(lectureNote);
                responseDTO = new MaterialResponseDTO(
                        savedLectureNote.getId(), savedLectureNote.getName(), savedLectureNote.getLink(), savedLectureNote.getFolder().getId()
                );
                break;

            case "OTHER":
                Other other = new Other();
                other.setFolder(folder);
                other.setName(materialData.getName());
                other.setLink(filePath);
                Other savedOther = otherRepo.save(other);
                responseDTO = new MaterialResponseDTO(
                        savedOther.getId(), savedOther.getName(), savedOther.getLink(), savedOther.getFolder().getId()
                );
                break;

            default:
                return null;
        }

        return responseDTO;
    }

}
