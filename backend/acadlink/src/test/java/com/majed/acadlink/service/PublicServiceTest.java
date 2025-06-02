package com.majed.acadlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import com.majed.acadlink.domain.entity.User;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.ApiResponse;
import com.majed.acadlink.dto.ErrorResponseDTO;
import com.majed.acadlink.dto.user.UserLoginDTO;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.dto.user.UserSignUpDTO;
import com.majed.acadlink.utility.JWTUtil;

@ExtendWith(MockitoExtension.class)
class PublicServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepo userRepo;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private PublicService publicService;

    private UserSignUpDTO validSignUpDTO;
    private UserLoginDTO validLoginDTO;
    private UserResponseDTO validUserResponse;
    private UserDetails mockUserDetails;
    private User mockUser;
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

        // Setup mock user
        mockUser = new User();
        mockUser.setId(testUserId);
        mockUser.setEmail("john.doe@example.com");
        mockUser.setUsername("johndoe");

        // Setup mock user details
        mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("johndoe")
                .password("password123")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void addUser_Success() {
        // Arrange
        when(userRepo.findByEmail(validSignUpDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepo.findByUsername(validSignUpDTO.getUserName())).thenReturn(Optional.empty());
        when(userService.createUser(any(UserSignUpDTO.class))).thenReturn(validUserResponse);

        // Act
        ResponseEntity<Object> response = publicService.addUser(validSignUpDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(validUserResponse, response.getBody());
        verify(userRepo, times(1)).findByEmail(validSignUpDTO.getEmail());
        verify(userRepo, times(1)).findByUsername(validSignUpDTO.getUserName());
        verify(userService, times(1)).createUser(validSignUpDTO);
    }

    @Test
    void addUser_EmailExists_ReturnsBadRequest() {
        // Arrange
        when(userRepo.findByEmail(validSignUpDTO.getEmail())).thenReturn(Optional.of(mockUser));
        // Don't mock username check since it should fail at email check

        // Act
        ResponseEntity<Object> response = publicService.addUser(validSignUpDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponseDTO);
        ErrorResponseDTO error = (ErrorResponseDTO) response.getBody();
        assertEquals("User exists with this email", error.getMessage());
        verify(userRepo, times(1)).findByEmail(validSignUpDTO.getEmail());
        verify(userRepo, times(0)).findByUsername(validSignUpDTO.getUserName());
    }

    @Test
    void addUser_UsernameExists_ReturnsBadRequest() {
        // Arrange
        when(userRepo.findByEmail(validSignUpDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepo.findByUsername(validSignUpDTO.getUserName())).thenReturn(Optional.of(mockUser));

        // Act
        ResponseEntity<Object> response = publicService.addUser(validSignUpDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponseDTO);
        ErrorResponseDTO error = (ErrorResponseDTO) response.getBody();
        assertEquals("User exists with this username", error.getMessage());
        verify(userRepo, times(1)).findByEmail(validSignUpDTO.getEmail());
        verify(userRepo, times(1)).findByUsername(validSignUpDTO.getUserName());
    }

    @Test
    void login_Success() {
        // Arrange
        String expectedToken = "test.jwt.token";
        when(userDetailsService.loadUserByUsername(validLoginDTO.getUsernameorEmail())).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(anyString())).thenReturn(expectedToken);
        // Don't mock authentication manager since it's void method

        // Act
        ResponseEntity<ApiResponse<String>> response = publicService.login(validLoginDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedToken, response.getBody().getData());
        verify(userDetailsService, times(1)).loadUserByUsername(validLoginDTO.getUsernameorEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken(anyString());
    }

    @Test
    void login_UserNotFound_ReturnsNotFound() {
        // Arrange
        when(userDetailsService.loadUserByUsername(validLoginDTO.getUsernameorEmail())).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse<String>> response = publicService.login(validLoginDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getError());
        verify(userDetailsService, times(1)).loadUserByUsername(validLoginDTO.getUsernameorEmail());
        verify(authenticationManager, times(0)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_InvalidCredentials_ReturnsBadRequest() {
        // Arrange
        when(userDetailsService.loadUserByUsername(validLoginDTO.getUsernameorEmail())).thenReturn(mockUserDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        ResponseEntity<ApiResponse<String>> response = publicService.login(validLoginDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Incorrect Password", response.getBody().getError());
        verify(userDetailsService, times(1)).loadUserByUsername(validLoginDTO.getUsernameorEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(0)).generateToken(anyString());
    }

    @Test
    void checkUsername_Exists_ReturnsTrue() {
        // Arrange
        String username = "johndoe";
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        ResponseEntity<Boolean> response = publicService.checkUsername(username);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(userRepo, times(1)).findByUsername(username);
    }

    @Test
    void checkUsername_NotExists_ReturnsFalse() {
        // Arrange
        String username = "nonexistent";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Boolean> response = publicService.checkUsername(username);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(!response.getBody());
        verify(userRepo, times(1)).findByUsername(username);
    }
} 