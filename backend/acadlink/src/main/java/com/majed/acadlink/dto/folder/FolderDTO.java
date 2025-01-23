package com.majed.acadlink.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class FolderDTO {
    private UUID id;
    private String name;
    private LocalDate createdAt;
}
