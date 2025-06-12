package com.majed.acadlink.api.v1.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.majed.acadlink.domain.entity.User;
import com.majed.acadlink.dto.emailverification.EmailVerificationRequest;
import com.majed.acadlink.dto.emailverification.EmailVerificationResponse;
import com.majed.acadlink.exception.EmailVerificationException;
import com.majed.acadlink.exception.VerificationCodeException;
import com.majed.acadlink.service.EmailService;
import com.majed.acadlink.service.UserService;
import com.majed.acadlink.service.VerificationCodeService;

@ExtendWith(MockitoExtension.class)
class EmailVerificationControllerTest {

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmailVerificationController emailVerificationController;

    private User testUser;
    private UUID userId;
    private String testEmail;
    private String testOtp;
    private EmailVerificationRequest validRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testEmail = "test@example.com";
        testOtp = "123456";
        
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail(testEmail);
        testUser.setEmailVerified(false);

        validRequest = new EmailVerificationRequest();
        validRequest.setEmail(testEmail);
        validRequest.setOtp(testOtp);
    }

    @Test
    void verifyEmail_Success() {
        // Arrange
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(verificationCodeService.isVerificationCodeValid(userId, testEmail, testOtp)).thenReturn(true);
        when(userService.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.verifyEmail(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isVerified());
        assertEquals("Email verified successfully", response.getBody().getMessage());
        verify(verificationCodeService).removeVerificationCode(userId, testEmail);
    }

    @Test
    void verifyEmail_UserNotFound() {
        // Arrange
        when(userService.findByEmail(testEmail)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.verifyEmail(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isVerified());
        assertEquals("User not found with email: " + testEmail, response.getBody().getMessage());
        verify(verificationCodeService, never()).removeVerificationCode(any(), anyString());
    }

    @Test
    void verifyEmail_AlreadyVerified() {
        // Arrange
        testUser.setEmailVerified(true);
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.verifyEmail(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isVerified());
        assertEquals("Email already verified", response.getBody().getMessage());
        verify(verificationCodeService, never()).isVerificationCodeValid(any(), anyString(), anyString());
        verify(verificationCodeService, never()).removeVerificationCode(any(), anyString());
    }

    @Test
    void verifyEmail_InvalidOtp() {
        // Arrange
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(verificationCodeService.isVerificationCodeValid(userId, testEmail, testOtp)).thenReturn(false);

        // Act
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.verifyEmail(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isVerified());
        assertEquals("Invalid verification code", response.getBody().getMessage());
        verify(verificationCodeService, never()).removeVerificationCode(any(), anyString());
    }

    @Test
    void resendVerification_Success() {
        // Arrange
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(verificationCodeService.generateAndStoreOTP(userId, testEmail)).thenReturn(testOtp);

        // Act
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.resendVerification(testEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isVerified());
        assertEquals("Verification code sent successfully", response.getBody().getMessage());
        verify(emailService).sendVerificationEmail(testEmail, testOtp);
    }

    @Test
    void resendVerification_UserNotFound() {
        // Arrange
        when(userService.findByEmail(testEmail)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.resendVerification(testEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isVerified());
        assertEquals("User not found with email: " + testEmail, response.getBody().getMessage());
        verify(verificationCodeService, never()).generateAndStoreOTP(any(), anyString());
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void resendVerification_AlreadyVerified() {
        // Arrange
        testUser.setEmailVerified(true);
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.resendVerification(testEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isVerified());
        assertEquals("Email already verified", response.getBody().getMessage());
        verify(verificationCodeService, never()).generateAndStoreOTP(any(), anyString());
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void resendVerification_EmailServiceError() {
        // Arrange
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(verificationCodeService.generateAndStoreOTP(userId, testEmail)).thenReturn(testOtp);
        doThrow(new EmailVerificationException("Failed to send email"))
                .when(emailService).sendVerificationEmail(testEmail, testOtp);

        // Act
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.resendVerification(testEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isVerified());
        assertEquals("Failed to send email", response.getBody().getMessage());
    }

    @Test
    void verifyEmail_InvalidRequest() {
        // Test with invalid email format
        EmailVerificationRequest request = new EmailVerificationRequest();
        request.setEmail("invalid-email");
        request.setOtp(testOtp);

        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.verifyEmail(request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Test with invalid OTP length
        request = new EmailVerificationRequest();
        request.setEmail(testEmail);
        request.setOtp("12345"); // Too short

        response = emailVerificationController.verifyEmail(request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Test with missing email
        request = new EmailVerificationRequest();
        request.setOtp(testOtp);

        response = emailVerificationController.verifyEmail(request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Test with missing OTP
        request = new EmailVerificationRequest();
        request.setEmail(testEmail);

        response = emailVerificationController.verifyEmail(request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void resendVerification_InvalidRequest() {
        // Test with invalid email format
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.resendVerification("invalid-email");
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Test with missing email parameter
        response = emailVerificationController.resendVerification(null);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void verifyEmail_VerificationCodeException() {
        // Arrange
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(verificationCodeService.isVerificationCodeValid(userId, testEmail, testOtp))
                .thenThrow(new VerificationCodeException("Verification code error"));

        // Act
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.verifyEmail(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isVerified());
        assertEquals("Verification code error: Verification code error", response.getBody().getMessage());
    }

    @Test
    void resendVerification_VerificationCodeException() {
        // Arrange
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(verificationCodeService.generateAndStoreOTP(userId, testEmail))
                .thenThrow(new VerificationCodeException("Failed to generate OTP"));

        // Act
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.resendVerification(testEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isVerified());
        assertEquals("Verification code error: Failed to generate OTP", response.getBody().getMessage());
    }

    @Test
    void verifyEmail_UnexpectedException() {
        // Arrange
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(verificationCodeService.isVerificationCodeValid(userId, testEmail, testOtp))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.verifyEmail(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isVerified());
        assertEquals("Verification failed due to an unexpected error", response.getBody().getMessage());
    }

    @Test
    void resendVerification_UnexpectedException() {
        // Arrange
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(verificationCodeService.generateAndStoreOTP(userId, testEmail))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<EmailVerificationResponse> response = emailVerificationController.resendVerification(testEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isVerified());
        assertEquals("Failed to resend verification code due to an unexpected error", response.getBody().getMessage());
    }
} 