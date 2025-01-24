package com.majed.acadlink.dto.material;

import com.majed.acadlink.enums.MaterialType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class MaterialAddDTO {
    private String name;
    private UUID folderId;
    private String link;
    private MaterialType type;
    private MultipartFile file;

}
