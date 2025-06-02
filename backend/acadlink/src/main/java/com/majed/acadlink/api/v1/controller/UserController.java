package com.majed.acadlink.api.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for user profile management endpoints.
 */
@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("user")
@Tag(name = "3. Profile Management", description = "Endpoints for managing user profiles")
public class UserController {
    private final UserService userService;

    /**
     * Constructor for UserController.
     *
     * @param userService the user service
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the user information.
     *
     * @return the response entity containing either an error response or the user response
     */
    @GetMapping("get-user")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUser() {
        return userService.findUser();
    }

}
