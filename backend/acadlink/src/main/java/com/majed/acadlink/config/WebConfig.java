package com.majed.acadlink.config;

import com.majed.acadlink.utility.MultipartFilePropertyEditor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class WebConfig {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Register the custom PropertyEditor for MultipartFile
        binder.registerCustomEditor(MultipartFile.class, new MultipartFilePropertyEditor());
    }
}
