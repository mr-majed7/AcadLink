package com.majed.acadlink.controller;

import com.majed.acadlink.dto.UserDTO;
import com.majed.acadlink.dto.UserSignUp;
import com.majed.acadlink.entitie.User;
import com.majed.acadlink.repository.UserRepo;
import com.majed.acadlink.service.UserDetailsServiceImpl;
import com.majed.acadlink.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepo userRepo;

    @PostMapping("/sign-up")
    public ResponseEntity<UserDTO> signUp(@RequestBody UserSignUp userData) {
        Optional<User> user = userRepo.findByEmail(userData.getEmail());

        if (user.isPresent()) {
            log.error("User exists with this email");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        user = userRepo.findByUsername(userData.getUserName());

        if (user.isPresent()) {
            log.error("User exists with this username");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (user.isEmpty()) {

            try {
                UserDTO addedUser = userService.createUser(userData);
                return new ResponseEntity<>(addedUser, HttpStatus.OK);
            } catch (Exception e) {
                log.error(e.toString());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return null;
    }
}
