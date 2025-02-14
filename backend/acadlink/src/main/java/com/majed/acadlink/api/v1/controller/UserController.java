package com.majed.acadlink.api.v1.controller;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.service.UserService;
import com.majed.acadlink.utility.GetUserUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("user")
@Tag(name = "2. Profile Management")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private GetUserUtil getUserUtil;

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
