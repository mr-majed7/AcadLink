package com.majed.acadlink.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    /**
     * This is a test api controller
     *
     * @return String
     */
    @GetMapping("/hello")
    public String test() {
        return "Hello, This is test controller";
    }
}
