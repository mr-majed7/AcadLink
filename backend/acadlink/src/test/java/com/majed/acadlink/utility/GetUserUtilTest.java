package com.majed.acadlink.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.majed.acadlink.domain.entity.User;
import com.majed.acadlink.domain.repository.UserRepo;

@ExtendWith(MockitoExtension.class)
class GetUserUtilTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private GetUserUtil getUserUtil;

    private User mockUser;
    private UUID testUserId;
    private String testUsername;
    private Authentication mockAuthentication;
    private SecurityContext mockSecurityContext;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUsername = "testuser";

        // Setup mock user
        mockUser = new User();
        mockUser.setId(testUserId);
        mockUser.setUsername(testUsername);
        mockUser.setEmail("test@example.com");

        // Setup mock authentication
        mockAuthentication = mock(Authentication.class);
        mockSecurityContext = mock(SecurityContext.class);
    }

    @Test
    void getAuthenticatedUser_Success() {
        // Arrange
        when(mockAuthentication.getName()).thenReturn(testUsername);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);
        when(userRepo.findByUsername(testUsername)).thenReturn(Optional.of(mockUser));

        // Act
        Optional<User> result = getUserUtil.getAuthenticatedUser();

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
        verify(userRepo, times(1)).findByUsername(testUsername);
    }

    @Test
    void getAuthenticatedUser_UserNotFound() {
        // Arrange
        when(mockAuthentication.getName()).thenReturn(testUsername);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);
        when(userRepo.findByUsername(testUsername)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = getUserUtil.getAuthenticatedUser();

        // Assert
        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findByUsername(testUsername);
    }

    @Test
    void getAuthenticatedUser_NoAuthentication() {
        // Arrange
        when(mockSecurityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(mockSecurityContext);

        // Act
        Optional<User> result = getUserUtil.getAuthenticatedUser();

        // Assert
        assertFalse(result.isPresent());
        verify(userRepo, times(0)).findByUsername(testUsername);
    }

    @Test
    void getAuthenticatedUser_EmptyUsername() {
        // Arrange
        when(mockAuthentication.getName()).thenReturn("");
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        // Act
        Optional<User> result = getUserUtil.getAuthenticatedUser();

        // Assert
        assertFalse(result.isPresent());
        verify(userRepo, times(0)).findByUsername(testUsername);
    }
} 