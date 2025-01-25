package com.majed.acadlink.dto.folder;

import com.majed.acadlink.enums.Privacy;
import lombok.Data;

@Data
public class FolderCreateDTO {
    private String name;
    private Privacy privacy;
}
