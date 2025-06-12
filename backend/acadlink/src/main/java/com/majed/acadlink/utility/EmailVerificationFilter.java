package com.majed.acadlink.utility;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.majed.acadlink.domain.entity.User;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.ApiResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Security filter that enforces email verification for protected endpoints in the AcadLink application.
 * This filter is part of the Spring Security filter chain and should be placed after the JWT filter.
 *
 * <p>Purpose:
 * 1. Ensures users have verified their email before accessing protected resources
 * 2. Prevents unauthorized access to protected endpoints
 * 3. Maintains security by enforcing email verification policy</p>
 *
 * <p>Filter Behavior:
 * - Skips verification for public endpoints and email verification endpoints
 * - Checks email verification status for authenticated users
 * - Returns 403 Forbidden if email is not verified
 * - Logs unauthorized access attempts for security monitoring</p>
 *
 * <p>Security Considerations:
 * - Must be placed after JWT filter to ensure user is authenticated
 * - Uses Spring Security's Authentication context
 * - Returns JSON error responses for better client handling
 * - Logs security events for monitoring</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationFilter extends OncePerRequestFilter {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error during email verification check";
    
    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;

    /**
     * Determines if the filter should be skipped for the current request.
     * The filter is skipped for public endpoints and email verification endpoints.
     *
     * <p>Skipped Paths:
     * - /v1/public/** (public endpoints)
     * - /auth/** (email verification endpoints)
     * - /public/login (login endpoint)
     * - /public/sign-up (signup endpoint)</p>
     *
     * @param request the HTTP request to check
     * @return true if the filter should be skipped, false otherwise
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip email verification check for public endpoints and email verification endpoints
        return path.startsWith("/v1/public/") || 
               path.startsWith("/auth/") || 
               path.equals("/public/login") || 
               path.equals("/public/sign-up");
    }

    /**
     * Processes the request to check email verification status.
     * This method is called for each request that passes the shouldNotFilter check.
     *
     * <p>Process Flow:
     * 1. Gets the current authentication from security context
     * 2. If user is authenticated, retrieves user details
     * 3. Checks if user's email is verified
     * 4. If not verified, returns 403 Forbidden
     * 5. If verified or not authenticated, continues the filter chain</p>
     *
     * <p>Error Handling:
     * - Returns 403 Forbidden if email not verified
     * - Returns 500 Internal Server Error for unexpected errors
     * - Logs all errors for monitoring
     * - Returns JSON error responses for better client handling</p>
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain to continue
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Skip filter for unauthenticated requests
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get username from authentication
        String username = authentication.getName();
        if (username == null || username.trim().isEmpty()) {
            log.error("Username is null or empty in authentication");
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, 
                INTERNAL_SERVER_ERROR_MESSAGE);
            return;
        }

        try {
            // Look up user
            User user = userRepo.findByUsername(username)
                    .orElse(null);

            // Handle user not found
            if (user == null) {
                log.error("User not found in email verification filter: {}", username);
                sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, 
                    INTERNAL_SERVER_ERROR_MESSAGE);
                return;
            }

            // Check email verification
            if (!user.isEmailVerified()) {
                log.warn("Unauthorized access attempt: User {} has not verified their email", username);
                sendErrorResponse(response, HttpStatus.FORBIDDEN, 
                    "Please verify your email before accessing this resource");
                return;
            }

            // User is verified, continue the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in email verification filter: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, 
                INTERNAL_SERVER_ERROR_MESSAGE);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(
            ApiResponse.error(message, status)));
    }
} 