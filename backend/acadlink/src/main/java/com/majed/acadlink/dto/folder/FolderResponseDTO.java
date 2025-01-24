package com.majed.acadlink.dto.folder;


import com.majed.acadlink.dto.material.MaterialResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class FolderResponseDTO {
    private UUID id;
    private String name;
    private LocalDate CreatedAt;
    private List<MaterialResponseDTO> materials;
}

