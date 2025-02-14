package com.majed.acadlink.utility;

import java.beans.PropertyEditorSupport;

public class MultipartFilePropertyEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        // If the string is empty, set it as null
        if (text == null || text.trim().isEmpty()) {
            setValue(null); // Nullify the value if it's empty
        } else {
            setValue(null); // MultipartFile binding should handle the actual file upload
        }
    }
}
