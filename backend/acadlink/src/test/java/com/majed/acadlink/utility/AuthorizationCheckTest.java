package com.majed.acadlink.utility;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.majed.acadlink.domain.entity.User;

@ExtendWith(MockitoExtension.class)
class AuthorizationCheckTest {

    @Mock
    private GetUserUtil getUserUtil;

    @InjectMocks
    private AuthorizationCheck authorizationCheck;

    private UUID testUserId;
    private UUID differentUserId;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        differentUserId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(testUserId);
        testUser.setUsername("testuser");
    }

    @Test
    void checkAuthorization_Success() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));

        // Act
        boolean result = authorizationCheck.checkAuthorization(testUserId);

        // Assert
        assertTrue(result);
        verify(getUserUtil, times(1)).getAuthenticatedUser();
    }

    @Test
    void checkAuthorization_DifferentUser() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.of(testUser));

        // Act
        boolean result = authorizationCheck.checkAuthorization(differentUserId);

        // Assert
        assertFalse(result);
        verify(getUserUtil, times(1)).getAuthenticatedUser();
    }

    @Test
    void checkAuthorization_NoAuthenticatedUser() {
        // Arrange
        when(getUserUtil.getAuthenticatedUser()).thenReturn(Optional.empty());

        // Act
        boolean result = authorizationCheck.checkAuthorization(testUserId);

        // Assert
        assertFalse(result);
        verify(getUserUtil, times(1)).getAuthenticatedUser();
    }
} 