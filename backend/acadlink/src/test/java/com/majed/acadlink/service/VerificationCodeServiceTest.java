package com.majed.acadlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.majed.acadlink.exception.VerificationCodeException;

@ExtendWith(MockitoExtension.class)
class VerificationCodeServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private VerificationCodeService verificationCodeService;

    private UUID userId;
    private String email;
    private String otp;
    private String redisKey;
    private String verificationCodeJson;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        email = "test@example.com";
        otp = "123456";
        redisKey = String.format("verification:email:%s:%s", userId, email);
        verificationCodeJson = "{\"code\":\"" + otp + "\",\"generatedAt\":1234567890,\"email\":\"" + email + "\"}";
    }

    @Test
    void generateOTP_ReturnsSixDigitCode() {
        // Act
        String generatedOtp = verificationCodeService.generateOTP();

        // Assert
        assertNotNull(generatedOtp);
        assertEquals(6, generatedOtp.length());
        assertTrue(generatedOtp.matches("\\d{6}"));
    }

    @Test
    void generateAndStoreOTP_Success() throws JsonProcessingException {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(any())).thenReturn(verificationCodeJson);

        // Act
        String generatedOtp = verificationCodeService.generateAndStoreOTP(userId, email);

        // Assert
        assertNotNull(generatedOtp);
        assertEquals(6, generatedOtp.length());
        verify(valueOperations).set(redisKey, verificationCodeJson, 5L, TimeUnit.MINUTES);
    }

    @Test
    void storeVerificationCode_Success() throws JsonProcessingException {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(any())).thenReturn(verificationCodeJson);

        // Act
        verificationCodeService.storeVerificationCode(userId, email, otp);

        // Assert
        verify(valueOperations).set(redisKey, verificationCodeJson, 5L, TimeUnit.MINUTES);
    }

    @Test
    void storeVerificationCode_JsonProcessingError() throws JsonProcessingException {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON error") {});

        // Act & Assert
        VerificationCodeException exception = assertThrows(
            VerificationCodeException.class,
            () -> verificationCodeService.storeVerificationCode(userId, email, otp)
        );
        assertEquals("Failed to store verification code", exception.getMessage());
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void getVerificationCode_Success() throws JsonProcessingException {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(verificationCodeJson);
        when(objectMapper.readValue(verificationCodeJson, VerificationCodeService.VerificationCodeData.class))
            .thenReturn(new VerificationCodeService.VerificationCodeData(otp, 1234567890L, email));

        // Act
        VerificationCodeService.VerificationCodeData result = verificationCodeService.getVerificationCode(userId, email);

        // Assert
        assertNotNull(result);
        assertEquals(otp, result.getCode());
        assertEquals(email, result.getEmail());
        assertEquals(1234567890L, result.getGeneratedAt());
    }

    @Test
    void getVerificationCode_NotFound() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(null);

        // Act
        VerificationCodeService.VerificationCodeData result = verificationCodeService.getVerificationCode(userId, email);

        // Assert
        assertNull(result);
    }

    @Test
    void getVerificationCode_JsonProcessingError() throws JsonProcessingException {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(verificationCodeJson);
        when(objectMapper.readValue(verificationCodeJson, VerificationCodeService.VerificationCodeData.class))
            .thenThrow(new JsonProcessingException("JSON error") {});

        // Act & Assert
        VerificationCodeException exception = assertThrows(
            VerificationCodeException.class,
            () -> verificationCodeService.getVerificationCode(userId, email)
        );
        assertEquals("Failed to read verification code", exception.getMessage());
    }

    @Test
    void isVerificationCodeValid_ValidCode() throws JsonProcessingException {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(verificationCodeJson);
        when(objectMapper.readValue(verificationCodeJson, VerificationCodeService.VerificationCodeData.class))
            .thenReturn(new VerificationCodeService.VerificationCodeData(otp, 1234567890L, email));

        // Act
        boolean isValid = verificationCodeService.isVerificationCodeValid(userId, email, otp);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isVerificationCodeValid_InvalidCode() throws JsonProcessingException {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(verificationCodeJson);
        when(objectMapper.readValue(verificationCodeJson, VerificationCodeService.VerificationCodeData.class))
            .thenReturn(new VerificationCodeService.VerificationCodeData(otp, 1234567890L, email));

        // Act
        boolean isValid = verificationCodeService.isVerificationCodeValid(userId, email, "999999");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isVerificationCodeValid_CodeNotFound() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(null);

        // Act
        boolean isValid = verificationCodeService.isVerificationCodeValid(userId, email, otp);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void removeVerificationCode_Success() {
        // Act
        verificationCodeService.removeVerificationCode(userId, email);

        // Assert
        verify(redisTemplate).delete(redisKey);
    }
} 