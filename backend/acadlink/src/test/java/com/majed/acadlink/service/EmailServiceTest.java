package com.majed.acadlink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException; // Import concrete MailException subclass
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.majed.acadlink.exception.EmailVerificationException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @Captor
    private ArgumentCaptor<Context> contextCaptor;

    private EmailService emailService;
    private String fromEmail;
    private String toEmail;
    private String otp;

    @BeforeEach
    void setUp() {
        fromEmail = "test@acadlink.com";
        toEmail = "user@example.com";
        otp = "123456";
        emailService = new EmailService(mailSender, templateEngine, fromEmail);
    }

    @Test
    void sendVerificationEmail_Success() throws MessagingException { // Assuming jakarta.mail.MessagingException for MimeMessageHelper setup
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("verification-email"), any(Context.class)))
                .thenReturn("<html>Verification email content</html>");

        // Act
        emailService.sendVerificationEmail(toEmail, otp);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq("verification-email"), contextCaptor.capture());
        verify(mailSender).send(mimeMessage);

        // Verify template context
        Context capturedContext = contextCaptor.getValue();
        assertEquals(otp, capturedContext.getVariable("otp"));
        assertEquals(5, capturedContext.getVariable("expirationMinutes")); // Assuming 5 is the default
    }

    @Test
    void sendVerificationEmail_MessagingException() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("verification-email"), any(Context.class)))
                .thenReturn("<html>Verification email content</html>");
        // Use a concrete Spring MailException subclass for the mock
        doThrow(new MailSendException("Failed to send email"))
                .when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        EmailVerificationException exception = assertThrows(
                EmailVerificationException.class,
                () -> emailService.sendVerificationEmail(toEmail, otp)
        );

        assertEquals("Failed to send verification email to " + toEmail, exception.getMessage());
        
        // Verify interactions that should have occurred
        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq("verification-email"), any(Context.class)); // Verify template processing was attempted
        verify(mailSender).send(any(MimeMessage.class)); // Verify mailSender.send was called
    }

    @Test
    void sendVerificationEmail_TemplateProcessingError() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage); // Still need to create MimeMessage first
        when(templateEngine.process(eq("verification-email"), any(Context.class)))
                .thenThrow(new RuntimeException("Template processing failed"));

        // Act & Assert
        assertThrows(
                RuntimeException.class, // Expecting the raw RuntimeException from template processing
                () -> emailService.sendVerificationEmail(toEmail, otp)
        );

        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq("verification-email"), any(Context.class));
        verify(mailSender, never()).send(any(MimeMessage.class)); // Send should not be called if template fails
    }

    @Test
    void sendVerificationEmail_InvalidEmail() {
        // Arrange
        String invalidEmail = "invalid-email";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendVerificationEmail(invalidEmail, otp)
        );

        assertEquals("Invalid email address format", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(templateEngine, never()).process(any(String.class), any(Context.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendVerificationEmail_NullOtp() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendVerificationEmail(toEmail, null)
        );

        assertEquals("OTP cannot be null or empty", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(templateEngine, never()).process(any(String.class), any(Context.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendVerificationEmail_EmptyOtp() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendVerificationEmail(toEmail, "")
        );

        assertEquals("OTP cannot be null or empty", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(templateEngine, never()).process(any(String.class), any(Context.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendVerificationEmail_NullEmail() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendVerificationEmail(null, otp)
        );

        assertEquals("Email address cannot be null or empty", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(templateEngine, never()).process(any(String.class), any(Context.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendVerificationEmail_EmptyEmail() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendVerificationEmail("", otp)
        );

        assertEquals("Email address cannot be null or empty", exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
        verify(templateEngine, never()).process(any(String.class), any(Context.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}