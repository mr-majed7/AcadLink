package com.majed.acadlink.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.majed.acadlink.exception.EmailVerificationException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for handling email operations in the AcadLink application.
 * This service manages the sending of verification emails using Spring's JavaMailSender
 * and Thymeleaf templates for email content.
 *
 * <p>The service is designed to:
 * 1. Send verification emails with OTP codes
 * 2. Use HTML templates for consistent email formatting
 * 3. Handle email sending errors gracefully
 * 4. Log email operations for monitoring</p>
 *
 * <p>Configuration:
 * - Uses Spring's JavaMailSender for email delivery
 * - Uses Thymeleaf for email template processing
 * - Sender email is configured via application properties</p>
 *
 */
@Service
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String fromEmail;

    /**
     * Constructs a new EmailService with required dependencies.
     *
     * @param mailSender the Spring JavaMailSender for sending emails
     * @param templateEngine the Thymeleaf template engine for processing email templates
     * @param fromEmail the sender email address, configured via spring.mail.username
     */
    public EmailService(JavaMailSender mailSender,
                        TemplateEngine templateEngine,
                        @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.fromEmail = fromEmail;
    }

    /**
     * Sends a verification email containing an OTP code to the specified recipient.
     * The email is sent as HTML content using a Thymeleaf template.
     *
     * <p>Process Flow:
     * 1. Creates a new MIME message
     * 2. Sets up email headers (from, to, subject)
     * 3. Processes the email template with OTP and expiration time
     * 4. Sends the email using JavaMailSender</p>
     *
     * <p>Error Handling:
     * - Logs errors if email sending fails
     * - Throws EmailVerificationException with detailed error message
     * - Includes original exception as cause for debugging</p>
     *
     * @param to the recipient's email address
     * @param otp the one-time password code to include in the email
     * @throws EmailVerificationException if email sending fails
     */
    public void sendVerificationEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Verify your AcadLink email address");

            // Create Thymeleaf context and set variables
            Context context = new Context();
            context.setVariable("otp", otp);
            context.setVariable("expirationMinutes", 5); // Should match OTP_EXPIRATION in VerificationCodeService

            // Process the email template
            String emailContent = templateEngine.process("verification-email", context);

            helper.setText(emailContent, true); // true indicates HTML content
            mailSender.send(message);

            log.info("Verification email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}: {}", to, e.getMessage());
            throw new EmailVerificationException("Failed to send verification email to " + to, e);
        }
    }

}
