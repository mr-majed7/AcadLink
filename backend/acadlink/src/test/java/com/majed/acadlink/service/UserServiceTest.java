package com.majed.acadlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.dto.user.UserSignUpDTO;
import com.majed.acadlink.utility.GetUserUtil;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private GetUserUtil getUserUtil;

    @InjectMocks
    private UserService userService;

    private UserSignUpDTO validSignUpDTO;
    private User mockUser;
    private UserResponseDTO expectedUserResponse;
    private UUID testUserId;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        // Setup valid sign up DTO
        validSignUpDTO = new UserSignUpDTO();
        validSignUpDTO.setFirstName("John");
        validSignUpDTO.setLastName("Doe");
        validSignUpDTO.setEmail("john.doe@example.com");
        validSignUpDTO.setUserName("johndoe");
        validSignUpDTO.setPassword("password123");
        validSignUpDTO.setInstitute("Test University");

        // Setup mock user
        mockUser = new User();
        mockUser.setId(testUserId);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setUsername("johndoe");
        mockUser.setPassword(passwordEncoder.encode("password123"));
        mockUser.setInstitute("Test University");
        mockUser.setCreatedAt(LocalDate.now());

        // Setup expected user response
        expectedUserResponse = new UserResponseDTO(
            testUserId,
            "John",
            "Doe",
            "Test University",
            "john.doe@example.com",
            "johndoe",
            LocalDate.now()
        );
    }

    @Test
    void createUser_Success() {
        // Arrange
        when(userRepo.save(any(User.class))).thenReturn(mockUser);

        // Act
        UserResponseDTO response = userService.createUser(validSignUpDTO);

        // Assert
        assertNotNull(response);
        assertEquals(expectedUserResponse.getId(), response.getId());
        assertEquals(expectedUserResponse.getFirstName(), response.getFirstName());
        assertEquals(expectedUserResponse.getLastName(), response.getLastName());
        assertEquals(expectedUserResponse.getEmail(), response.getEmail());
        assertEquals(expectedUserResponse.getUserName(), response.getUserName());
        assertEquals(expectedUserResponse.getInstitute(), response.getInstitute());
        assertEquals(expectedUserResponse.getCreatedAt(), response.getCreatedAt());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void createUser_RepositoryError_ReturnsNull() {
        // Arrange
        when(userRepo.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        UserResponseDTO response = userService.createUser(validSignUpDTO);

        // Assert
        assertNull(response);
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void findUser_Success() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(mockUser));

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userService.findUser();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedUserResponse, response.getBody().getData());
        verify(getUserUtil, times(1)).getAuthenticatedUser();
    }

    @Test
    void findUser_NotAuthenticated_ReturnsBadRequest() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = userService.findUser();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not logged in", response.getBody().getError());
        verify(getUserUtil, times(1)).getAuthenticatedUser();
    }
} 