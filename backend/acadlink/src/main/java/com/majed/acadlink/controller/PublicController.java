package com.majed.acadlink.controller;

import com.majed.acadlink.dto.UserDTO;
import com.majed.acadlink.dto.UserLogin;
import com.majed.acadlink.dto.UserSignUp;
import com.majed.acadlink.repository.UserRepo;
import com.majed.acadlink.service.UserDetailsServiceImpl;
import com.majed.acadlink.service.UserService;
import com.majed.acadlink.utility.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/sign-up")
    public ResponseEntity<UserDTO> signUp(@RequestBody UserSignUp userData) {
        try {
            // Check if a user with the provided email exists
            userDetailsService.loadUserByUsername(userData.getEmail());
            log.error("User exists with this email");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException e) {
            // Email not found, continue with username check
        }

        try {
            // Check if a user with the provided username exists
            userDetailsService.loadUserByUsername(userData.getUserName());
            log.error("User exists with this username");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException e) {
            // Username not found, proceed with sign-up
        }

        try {
            UserDTO addedUser = userService.createUser(userData);
            return new ResponseEntity<>(addedUser, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> logIn(@RequestBody UserLogin userData) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userData.getUsernameorEmail(), userData.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(userData.getUsernameorEmail());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
