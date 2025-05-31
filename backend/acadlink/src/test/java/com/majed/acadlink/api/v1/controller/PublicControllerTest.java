package com.majed.acadlink.api.v1.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import com.majed.acadlink.dto.ErrorResponseDTO;
import com.majed.acadlink.dto.user.UserLoginDTO;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.dto.user.UserSignUpDTO;
import com.majed.acadlink.service.PublicService;

import io.vavr.control.Either;

@ExtendWith(MockitoExtension.class)
class PublicControllerTest {

    @Mock
    private PublicService publicService;

    @InjectMocks
    private PublicController publicController;

    private UserSignUpDTO validSignUpDTO;
    private UserLoginDTO validLoginDTO;
    private UserResponseDTO validUserResponse;
    private UUID testUserId;

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

        // Setup valid login DTO
        validLoginDTO = new UserLoginDTO("johndoe", "password123");

        // Setup valid user response
        validUserResponse = new UserResponseDTO(
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
    void signUp_Success() {
        // Arrange
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(
            Either.right(validUserResponse), 
            HttpStatus.CREATED
        );
        when(publicService.addUser(any(UserSignUpDTO.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<Object> response = publicController.signUp(validSignUpDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Either);
        Either<?, ?> responseBody = (Either<?, ?>) response.getBody();
        assertTrue(responseBody.isRight());
        assertEquals(validUserResponse, responseBody.get());
        verify(publicService, times(1)).addUser(validSignUpDTO);
    }

    @Test
    void signUp_UserExists_ReturnsBadRequest() {
        // Arrange
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(
            Either.left(new ErrorResponseDTO("User exists with this email", HttpStatus.BAD_REQUEST.value())),
            HttpStatus.BAD_REQUEST
        );
        when(publicService.addUser(any(UserSignUpDTO.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<Object> response = publicController.signUp(validSignUpDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Either);
        Either<?, ?> responseBody = (Either<?, ?>) response.getBody();
        assertTrue(responseBody.isLeft());
        verify(publicService, times(1)).addUser(validSignUpDTO);
    }

    @Test
    void login_Success() {
        // Arrange
        String expectedToken = "test.jwt.token";
        ResponseEntity<ApiResponse<String>> expectedResponse = ApiResponse.success(expectedToken, HttpStatus.OK);
        when(publicService.login(any(UserLoginDTO.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<String>> response = publicController.logIn(validLoginDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedToken, response.getBody().getData());
        verify(publicService, times(1)).login(validLoginDTO);
    }

    @Test
    void login_InvalidCredentials_ReturnsBadRequest() {
        // Arrange
        ResponseEntity<ApiResponse<String>> expectedResponse = 
            ApiResponse.error("Incorrect Password", HttpStatus.BAD_REQUEST);
        when(publicService.login(any(UserLoginDTO.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<String>> response = publicController.logIn(validLoginDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Incorrect Password", response.getBody().getError());
        verify(publicService, times(1)).login(validLoginDTO);
    }

    @Test
    void checkUsername_Exists_ReturnsTrue() {
        // Arrange
        String username = "johndoe";
        when(publicService.checkUsername(username)).thenReturn(new ResponseEntity<>(true, HttpStatus.OK));

        // Act
        ResponseEntity<Boolean> response = publicController.checkUsername(username);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(publicService, times(1)).checkUsername(username);
    }

    @Test
    void checkUsername_NotExists_ReturnsFalse() {
        // Arrange
        String username = "nonexistent";
        when(publicService.checkUsername(username)).thenReturn(new ResponseEntity<>(false, HttpStatus.OK));

        // Act
        ResponseEntity<Boolean> response = publicController.checkUsername(username);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
        verify(publicService, times(1)).checkUsername(username);
    }
} 