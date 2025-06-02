package com.majed.acadlink.api.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.majed.acadlink.dto.emailverification.EmailVerificationRequest;
import com.majed.acadlink.dto.emailverification.EmailVerificationResponse;
import com.majed.acadlink.exception.EmailVerificationException;
import com.majed.acadlink.exception.ResourceNotFoundException;
import com.majed.acadlink.service.EmailService;
import com.majed.acadlink.service.UserService;
import com.majed.acadlink.service.VerificationCodeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for handling email verification operations.
 * This controller provides endpoints for:
 * 1. Verifying email addresses using OTP
 * 2. Resending verification codes
 */
@Tag(name = "2. Email Verification", description = "Endpoints for email verification and OTP management")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationController {
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;
    private final UserService userService;

    /**
     * Verifies a user's email address using the provided OTP.
     *
     * @param request the email verification request containing email and OTP
     * @return response indicating the verification status
     */
    @Operation(
        summary = "Verify email address",
        description = "Verifies a user's email address using the provided OTP code",
        tags = {"2. Email Verification"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Email verified successfully",
            content = @Content(schema = @Schema(implementation = EmailVerificationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid verification code or user not found",
            content = @Content(schema = @Schema(implementation = EmailVerificationResponse.class))
        )
    })
    @PostMapping("/verify-email")
    public ResponseEntity<EmailVerificationResponse> verifyEmail(
            @Valid @RequestBody EmailVerificationRequest request) {
        try {
            var user = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

            if (user.isEmailVerified()) {
                return ResponseEntity.ok(new EmailVerificationResponse(true, "Email already verified"));
            }

            boolean isValid = verificationCodeService.isVerificationCodeValid(
                    user.getId(),
                    request.getEmail(),
                    request.getOtp()
            );

            if (isValid) {
                user.setEmailVerified(true);
                userService.save(user);
                verificationCodeService.removeVerificationCode(user.getId(), request.getEmail());

                return ResponseEntity.ok(new EmailVerificationResponse(true, "Email verified successfully"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new EmailVerificationResponse(false, "Invalid verification code"));
            }
        } catch (ResourceNotFoundException e) {
            log.error("User not found during email verification: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new EmailVerificationResponse(false, e.getMessage()));
        } catch (EmailVerificationException e) {
            log.error("Email verification error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new EmailVerificationResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during email verification: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new EmailVerificationResponse(false, "Verification failed due to an unexpected error"));
        }
    }

    /**
     * Resends the verification code to the user's email address.
     *
     * @param email the email address to resend the verification code to
     * @return response indicating the status of the resend operation
     */
    @Operation(
        summary = "Resend verification code",
        description = "Resends the verification code to the user's email address",
        tags = {"2. Email Verification"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Verification code sent successfully",
            content = @Content(schema = @Schema(implementation = EmailVerificationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "User not found or email already verified",
            content = @Content(schema = @Schema(implementation = EmailVerificationResponse.class))
        )
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<EmailVerificationResponse> resendVerification(
            @Parameter(description = "Email address to resend verification code to", required = true)
            @RequestParam @Valid String email) {
        try {
            var user = userService.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

            if (user.isEmailVerified()) {
                return ResponseEntity.ok(new EmailVerificationResponse(true, "Email already verified"));
            }

            String otp = verificationCodeService.generateAndStoreOTP(user.getId(), email);
            emailService.sendVerificationEmail(email, otp);

            return ResponseEntity.ok(new EmailVerificationResponse(true, "Verification code sent successfully"));
        } catch (ResourceNotFoundException e) {
            log.error("User not found during resend verification: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new EmailVerificationResponse(false, e.getMessage()));
        } catch (EmailVerificationException e) {
            log.error("Email verification error during resend: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new EmailVerificationResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during resend verification: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new EmailVerificationResponse(false, "Failed to resend verification code due to an unexpected error"));
        }
    }
}
