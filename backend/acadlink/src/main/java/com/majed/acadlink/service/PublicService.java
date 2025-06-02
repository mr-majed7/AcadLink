package com.majed.acadlink.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.ErrorResponseDTO;
import com.majed.acadlink.dto.user.UserLoginDTO;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.dto.user.UserSignUpDTO;
import com.majed.acadlink.utility.JWTUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class for handling public operations such as user sign-up and login.
 */
@Service
@Slf4j
public class PublicService {
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final JWTUtil jwtUtil;

    /**
     * Constructor for PublicService.
     *
     * @param userDetailsService    the user details service
     * @param userService           the user service
     * @param authenticationManager the authentication manager
     * @param userRepo              the user repository
     * @param jwtUtil               the JWT utility
     */
    public PublicService(UserDetailsServiceImpl userDetailsService, UserService userService,
                         AuthenticationManager authenticationManager, UserRepo userRepo, JWTUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Adds a new user to the system.
     *
     * @param userData the user sign-up data
     * @return the response entity containing the user response or an error status
     */
    public ResponseEntity<Object> addUser(UserSignUpDTO userData) {
        // Check if email exists
        if (userRepo.findByEmail(userData.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("User exists with this email",
                            HttpStatus.BAD_REQUEST.value()));
        }

        // Check if username exists
        if (userRepo.findByUsername(userData.getUserName()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("User exists with this username",
                            HttpStatus.BAD_REQUEST.value()));
        }

        try {
            UserResponseDTO addedUser = userService.createUser(userData);
            if (addedUser == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO("Failed to create user",
                                HttpStatus.BAD_REQUEST.value()));
            }
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(addedUser);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Error creating user: " + e.getMessage(),
                            HttpStatus.BAD_REQUEST.value()));
        }
    }

    /**
     * Logs in a user and generates a JWT token.
     *
     * @param userData the user login data
     * @return the response entity containing the JWT token or an error status
     */
    public ResponseEntity<ApiResponse<String>> login(UserLoginDTO userData) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userData.getUsernameorEmail());

            if (userDetails == null) {
                return ApiResponse.error("User not found", HttpStatus.NOT_FOUND);
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userData.getUsernameorEmail(), userData.getPassword())
            );

            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            return ApiResponse.success(jwt, HttpStatus.OK);

        } catch (BadCredentialsException e) {
            return ApiResponse.error("Incorrect Password", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.toString());
            return ApiResponse.error("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Checks if a username exists in the system.
     *
     * @param userName the username to check
     * @return the response entity containing a boolean indicating if the username exists
     */
    public ResponseEntity<Boolean> checkUsername(String userName) {
        if (userRepo.findByUsername(userName).isPresent()) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }

}
