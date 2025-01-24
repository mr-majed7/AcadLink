package com.majed.acadlink.dto.material;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MaterialResponseDTO {
    private UUID id;
    private String name;
    private String link;
    private UUID folderId;

}
