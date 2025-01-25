package com.majed.acadlink.dto.material;

import com.majed.acadlink.enums.MaterialType;
import com.majed.acadlink.enums.Privacy;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MaterialResponseDTO {
    private UUID id;
    private String name;
    private String link;
    private MaterialType type;
    private Privacy privacy;
    private UUID folderId;

}
