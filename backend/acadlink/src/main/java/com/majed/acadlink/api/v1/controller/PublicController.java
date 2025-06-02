package com.majed.acadlink.api.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.user.UserLoginDTO;
import com.majed.acadlink.dto.user.UserSignUpDTO;
import com.majed.acadlink.service.PublicService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for public endpoints such as Sign Up and Sign In.
 */
@Tag(name = "1. Public", description = "Public endpoints to Sign Up, Sign In")
@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {
    private final PublicService publicService;

    /**
     * Constructor for PublicController.
     *
     * @param publicService the public service
     */
    public PublicController(PublicService publicService) {
        this.publicService = publicService;
    }

    /**
     * Signs up a new user.
     *
     * @param userData the user data for sign up
     * @return the response entity containing the user response or an error status
     */
    @Operation(summary = "Sign up a new user", tags = {"1. Public"})
    @PostMapping("/sign-up")
    public ResponseEntity<Object> signUp(@RequestBody UserSignUpDTO userData) {
        return publicService.addUser(userData);
    }

    /**
     * Logs in a user.
     *
     * @param userData the user data for login
     * @return the response entity containing the JWT token or an error status
     */
    @Operation(summary = "Log in a user", tags = {"1. Public"})
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> logIn(@RequestBody UserLoginDTO userData) {
        return publicService.login(userData);
    }

    /**
     * Checks if a username exists.
     *
     * @param userName the username to check
     * @return the response entity containing a boolean indicating if the username exists
     */
    @Operation(summary = "Check if username exists", tags = {"1. Public"})
    @GetMapping("check-username/{userName}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String userName) {
        return publicService.checkUsername(userName);
    }

}
