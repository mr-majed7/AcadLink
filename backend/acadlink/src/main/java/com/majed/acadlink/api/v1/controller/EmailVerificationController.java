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
 * Controller for handling email verification operations in the AcadLink application.
 * This controller provides endpoints for email verification and OTP management.
 * It integrates with the following services:
 * - VerificationCodeService: Manages OTP generation and validation
 * - EmailService: Handles sending verification emails
 * - UserService: Manages user data and verification status
 *
 * <p>The controller enforces email verification as a security measure to ensure
 * that users have access to the email addresses they register with. This helps
 * prevent spam accounts and ensures reliable communication with users.</p>
 *
 * <p>Security Considerations:
 * - All endpoints are public (no authentication required)
 * - Rate limiting should be implemented at the application level
 * - OTP codes expire after 5 minutes
 * - Failed verification attempts are logged for security monitoring</p>
 *
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
     * Verifies a user's email address using the provided OTP code.
     * This endpoint is part of the email verification flow and should be called
     * after the user receives the verification code via email.
     *
     * <p>Process Flow:
     * 1. Validates the user exists with the provided email
     * 2. Checks if the email is already verified
     * 3. Validates the OTP code against the stored code
     * 4. Updates the user's verification status if valid
     * 5. Removes the used verification code from storage</p>
     *
     * <p>Error Handling:
     * - Returns 400 if user not found
     * - Returns 400 if OTP is invalid
     * - Returns 400 if email is already verified
     * - Returns 400 for any unexpected errors</p>
     *
     * @param request the email verification request containing:
     *               - email: The email address to verify
     *               - otp: The one-time password code received via email
     * @return ResponseEntity containing:
     *         - 200 OK with success message if verification successful
     *         - 400 Bad Request with error message if verification fails
     * @throws ResourceNotFoundException if user not found
     * @throws EmailVerificationException if verification process fails
     */
    @Operation(
        summary = "Verify email address",
        description = "Verifies a user's email address using the provided OTP code. " +
                     "The OTP code must be entered within 5 minutes of being sent.",
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
     * This endpoint can be used if the original verification code expires
     * or if the user did not receive the initial verification email.
     *
     * <p>Process Flow:
     * 1. Validates the user exists with the provided email
     * 2. Checks if the email is already verified
     * 3. Generates a new OTP code
     * 4. Stores the new code with expiration
     * 5. Sends a new verification email</p>
     *
     * <p>Error Handling:
     * - Returns 400 if user not found
     * - Returns 400 if email is already verified
     * - Returns 400 if email sending fails
     * - Returns 400 for any unexpected errors</p>
     *
     * @param email the email address to resend the verification code to.
     *             Must be a valid email format and belong to an existing user
     * @return ResponseEntity containing:
     *         - 200 OK with success message if code sent successfully
     *         - 400 Bad Request with error message if resend fails
     * @throws ResourceNotFoundException if user not found
     * @throws EmailVerificationException if email sending fails
     */
    @Operation(
        summary = "Resend verification code",
        description = "Resends the verification code to the user's email address. " +
                     "Use this endpoint if the original code expired or was not received.",
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
