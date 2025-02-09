package com.majed.acadlink.dto.folder;

import com.majed.acadlink.enums.Privacy;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Data
public class UpdateFolderResponseDTO {
    private UUID id;
    private String name;
    private LocalDate CreatedAt;
    private Privacy privacy;
}
