package com.majed.acadlink.api.v1.controller;

import com.majed.acadlink.dto.ErrorResponseDTO;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user profile management endpoints.
 */
@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("user")
@Tag(name = "2. Profile Management")
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
    public ResponseEntity<Either<ErrorResponseDTO, UserResponseDTO>> getUser() {
        return userService.findUser();
    }

}
