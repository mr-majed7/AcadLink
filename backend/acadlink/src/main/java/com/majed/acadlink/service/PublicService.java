package com.majed.acadlink.service;

import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.ErrorResponseDTO;
import com.majed.acadlink.dto.user.UserLoginDTO;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.dto.user.UserSignUpDTO;
import com.majed.acadlink.utility.JWTUtil;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
    public ResponseEntity<Either<ErrorResponseDTO, UserResponseDTO>> addUser(UserSignUpDTO userData) {
        if (userDetailsService.loadUserByUsername(userData.getEmail()) != null) {
            return new ResponseEntity<>(
                    Either.left(new ErrorResponseDTO("User exists with this email",
                            HttpStatus.BAD_REQUEST.value())),
                    HttpStatus.BAD_REQUEST);
        }

        if (userDetailsService.loadUserByUsername(userData.getUserName()) != null) {
            return new ResponseEntity<>(
                    Either.left(new ErrorResponseDTO("User exists with this username",
                            HttpStatus.BAD_REQUEST.value())),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            UserResponseDTO addedUser = userService.createUser(userData);
            return new ResponseEntity<>(Either.right(addedUser), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Logs in a user and generates a JWT token.
     *
     * @param userData the user login data
     * @return the response entity containing the JWT token or an error status
     */
    public ResponseEntity<Either<ErrorResponseDTO, String>> login(UserLoginDTO userData) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userData.getUsernameorEmail());

            if (userDetails == null) {
                return new ResponseEntity<>(
                        Either.left(new ErrorResponseDTO("Invalid username or email",
                                HttpStatus.BAD_REQUEST.value())),
                        HttpStatus.BAD_REQUEST);
            }
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userData.getUsernameorEmail(), userData.getPassword())
            );
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(Either.right(jwt), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(
                    Either.left(new ErrorResponseDTO("Incorrect Password",
                            HttpStatus.BAD_REQUEST.value())),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(
                    Either.left(new ErrorResponseDTO("Unable to Process Request",
                            HttpStatus.INTERNAL_SERVER_ERROR.value())),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
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
