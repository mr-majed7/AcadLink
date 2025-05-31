package com.majed.acadlink.api.v1.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UUID testUserId;
    private UserResponseDTO sampleUser;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        // Setup sample user
        sampleUser = new UserResponseDTO(
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
    void getUser_Success() {
        // Arrange
        ResponseEntity<ApiResponse<UserResponseDTO>> expectedResponse = 
            ApiResponse.success(sampleUser, HttpStatus.OK);
        when(userService.findUser()).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = 
            userController.getUser();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleUser, response.getBody().getData());
        verify(userService, times(1)).findUser();
    }

    @Test
    void getUser_UserNotAuthenticated() {
        // Arrange
        ResponseEntity<ApiResponse<UserResponseDTO>> expectedResponse = 
            ApiResponse.error("User not logged in", HttpStatus.BAD_REQUEST);
        when(userService.findUser()).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<UserResponseDTO>> response = 
            userController.getUser();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not logged in", response.getBody().getError());
        verify(userService, times(1)).findUser();
    }
} 