package com.majed.acadlink.api.v1.controller;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.user.UserLoginDTO;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.dto.user.UserSignUpDTO;
import com.majed.acadlink.service.UserDetailsServiceImpl;
import com.majed.acadlink.service.UserService;
import com.majed.acadlink.utility.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "1. Public", description = "Public endpoints to Sign Up, Sign In")
@Slf4j
@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/public")
public class PublicController {
    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final JWTUtil jwtUtil;

    public PublicController(UserDetailsServiceImpl userDetailsService, UserService userService,
                            AuthenticationManager authenticationManager, UserRepo userRepo, JWTUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Sign up a new user", tags = {"1. Public"})
    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDTO> signUp(@RequestBody UserSignUpDTO userData) {
        try {
            userDetailsService.loadUserByUsername(userData.getEmail());
            log.error("User exists with this email");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException e) {
            log.error(e.toString());
        }

        try {
            userDetailsService.loadUserByUsername(userData.getUserName());
            log.error("User exists with this username");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException e) {
            log.error(e.toString());
        }

        try {
            UserResponseDTO addedUser = userService.createUser(userData);
            return new ResponseEntity<>(addedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Log in a user", tags = {"1. Public"})
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

    @Operation(summary = "Check if username exists", tags = {"1. Public"})
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
