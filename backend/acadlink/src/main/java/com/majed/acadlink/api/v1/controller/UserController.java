package com.majed.acadlink.api.v1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.service.UserService;
import com.majed.acadlink.utility.GetUserUtil;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("user")
@Tag(name = "2. Profile Management")
public class UserController {
    private final UserService userService;
    private final GetUserUtil getUserUtil;

    public UserController(UserService userService, GetUserUtil getUserUtil) {
        this.userService = userService;
        this.getUserUtil = getUserUtil;
    }

    @GetMapping("get-user")
    public ResponseEntity<UserResponseDTO> getUser() {
        User user = getUserUtil.getAuthenticatedUser().get();

        UserResponseDTO response = new UserResponseDTO(user.getId(),
                user.getFirstName(), user.getLastName(), user.getInstitute(), user.getEmail(),
                user.getUsername(), user.getCreatedAt()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
