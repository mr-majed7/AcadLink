package com.majed.acadlink.api.v1.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.user.UserLoginDTO;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.dto.user.UserSignUpDTO;
import com.majed.acadlink.service.UserDetailsServiceImpl;
import com.majed.acadlink.service.UserService;
import com.majed.acadlink.utility.JWTUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin("http://localhost:3000")
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
    public ResponseEntity<UserResponseDTO> signUp(@RequestBody UserSignUpDTO userData) {
        try {
            userDetailsService.loadUserByUsername(userData.getEmail());
            log.error("User exists with this email");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException e) {
        }

        try {
            userDetailsService.loadUserByUsername(userData.getUserName());
            log.error("User exists with this username");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException e) {
        }

        try {
            UserResponseDTO addedUser = userService.createUser(userData);
            return new ResponseEntity<>(addedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> logIn(@RequestBody UserLoginDTO userData) {
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

    @GetMapping("check-username/{userName}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String userName) {
        Optional<User> user = userRepo.findByUsername(userName);

        if (user.isPresent()) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
    }
}
