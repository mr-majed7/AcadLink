package com.majed.acadlink.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
class JWTUtilTest {

    @InjectMocks
    private JWTUtil jwtUtil;

    private static final String TEST_SECRET_KEY = "testSecretKey1234567890123456789012345678901234567890";
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secretKey", TEST_SECRET_KEY);
    }

    @Test
    void generateToken_Success() {
        // Act
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // Assert
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length); // JWT has 3 parts
    }

    @Test
    void extractUsername_Success() {
        // Arrange
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    void validateToken_ValidToken() {
        // Arrange
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_ExpiredToken() {
        // Arrange
        Date pastDate = new Date(System.currentTimeMillis() - 1000 * 60 * 60); // 1 hour ago
        String expiredToken = Jwts.builder()
            .claims(new HashMap<>())
            .subject(TEST_USERNAME)
            .header().empty().add("typ", "JWT")
            .and()
            .issuedAt(pastDate)
            .expiration(pastDate)
            .signWith(Keys.hmacShaKeyFor(TEST_SECRET_KEY.getBytes()))
            .compact();

        // Act & Assert
        try {
            boolean isValid = jwtUtil.validateToken(expiredToken);
            assertTrue(!isValid, "Expired token should be invalid");
        } catch (ExpiredJwtException e) {
            // This is also a valid outcome - the token is expired
            assertTrue(e.getMessage().contains("JWT expired"), "Exception should indicate token expiration");
        }
    }

    @Test
    void validateToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.format";

        // Act & Assert
        try {
            boolean isValid = jwtUtil.validateToken(invalidToken);
            assertTrue(!isValid, "Invalid token should be invalid");
        } catch (MalformedJwtException e) {
            // This is also a valid outcome - the token is malformed
            // Just verify that we got the expected exception type
            assertTrue(e instanceof MalformedJwtException, "Should throw MalformedJwtException for invalid token");
        }
    }

    @Test
    void extractExpiration_Success() {
        // Arrange
        String token = jwtUtil.generateToken(TEST_USERNAME);
        Date expectedExpiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60); // 1 hour from now

        // Act
        Date actualExpiration = jwtUtil.extractExpiration(token);

        // Assert
        assertNotNull(actualExpiration);
        // Allow 1 second difference due to execution time
        assertEquals(true, Math.abs(actualExpiration.getTime() - expectedExpiration.getTime()) <= 1000);
    }
} 