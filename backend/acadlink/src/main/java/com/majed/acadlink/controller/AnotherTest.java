package com.majed.acadlink.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnotherTest {
    @GetMapping
    public String test() {
        return "This is another test";
    }
}
