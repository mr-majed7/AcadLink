package com.majed.acadlink.dto.material;

import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.enums.Privacy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class MaterialAddDTO {
    private String name;
    private UUID folderId;
    private MaterialType type;
    @Schema(nullable = true)
    private String link;
    @Schema(nullable = true)
    private MultipartFile file;
    private Privacy privacy;


}
