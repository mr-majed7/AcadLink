package com.majed.acadlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.majed.acadlink.domain.entity.User;
import com.majed.acadlink.domain.repository.UserRepo;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User mockUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        // Setup mock user
        mockUser = new User();
        mockUser.setId(testUserId);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setUsername("johndoe");
        mockUser.setPassword("encodedPassword123");
        mockUser.setInstitute("Test University");
    }

    @Test
    void loadUserByUsername_WithEmail_Success() {
        // Arrange
        String email = "john.doe@example.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(mockUser.getUsername(), userDetails.getUsername());
        assertEquals(mockUser.getPassword(), userDetails.getPassword());
        verify(userRepo, times(1)).findByEmail(email);
        verify(userRepo, times(0)).findByUsername(anyString());
    }

    @Test
    void loadUserByUsername_WithUsername_Success() {
        // Arrange
        String username = "johndoe";
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(mockUser.getUsername(), userDetails.getUsername());
        assertEquals(mockUser.getPassword(), userDetails.getPassword());
        verify(userRepo, times(0)).findByEmail(anyString());
        verify(userRepo, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WithEmail_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email)
        );
        assertEquals("User not found with email: " + email, exception.getMessage());
        verify(userRepo, times(1)).findByEmail(email);
        verify(userRepo, times(0)).findByUsername(anyString());
    }

    @Test
    void loadUserByUsername_WithUsername_UserNotFound() {
        // Arrange
        String username = "nonexistent";
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username)
        );
        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(userRepo, times(0)).findByEmail(anyString());
        verify(userRepo, times(1)).findByUsername(username);
    }
} 