package com.majed.acadlink.service;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.dto.user.UserSignUpDTO;
import com.majed.acadlink.utility.GetUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for handling user-related operations.
 */
@Service
@Slf4j
public class UserService {
    private static final PasswordEncoder passWordEncoder = new BCryptPasswordEncoder();

    private final UserRepo userRepo;
    private final GetUserUtil getUserUtil;

    /**
     * Constructor for UserService.
     *
     * @param userRepo    the user repository
     * @param getUserUtil the utility for getting the authenticated user
     */
    public UserService(
            UserRepo userRepo,
            GetUserUtil getUserUtil
    ) {
        this.userRepo = userRepo;
        this.getUserUtil = getUserUtil;
    }

    /**
     * Creates a new user in the system.
     *
     * @param userData the user sign-up data
     * @return the response DTO containing the created user information
     */
    public UserResponseDTO createUser(UserSignUpDTO userData) {

        User user = new User();
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setInstitute(userData.getInstitute());
        user.setEmail(userData.getEmail());
        user.setUsername(userData.getUserName());
        user.setPassword(passWordEncoder.encode(userData.getPassword()));

        try {
            User savedUser = userRepo.save(user);
            return new UserResponseDTO(
                    savedUser.getId(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    savedUser.getInstitute(),
                    savedUser.getEmail(),
                    savedUser.getUsername(),
                    savedUser.getCreatedAt()
            );
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }
    }

    /**
     * Finds the authenticated user.
     *
     * @return the response entity containing either an error response or the user response
     */
    public ResponseEntity<ApiResponse<UserResponseDTO>> findUser() {
        Optional<User> getUser = getUserUtil.getAuthenticatedUser();

        if (getUser.isEmpty()) {
            return ApiResponse.error("User not logged in", HttpStatus.BAD_REQUEST);
        }

        User user = getUser.get();

        UserResponseDTO response = new UserResponseDTO(user.getId(),
                user.getFirstName(), user.getLastName(), user.getInstitute(), user.getEmail(),
                user.getUsername(), user.getCreatedAt()
        );
        return ApiResponse.success(response, HttpStatus.OK);
    }
}
