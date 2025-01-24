package com.majed.acadlink.service;

import com.majed.acadlink.dto.material.MaterialAddDTO;
import com.majed.acadlink.dto.material.MaterialResponseDTO;
import com.majed.acadlink.repository.*;
import com.majed.acadlink.utility.SaveMaterialUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

}

