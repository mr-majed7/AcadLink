package com.majed.acadlink.dto.folder;

import com.majed.acadlink.enums.Privacy;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AllFolderResponseDTO {
    private UUID id;
    private String name;
    private LocalDate createdAt;
    private Privacy privacy;
}
