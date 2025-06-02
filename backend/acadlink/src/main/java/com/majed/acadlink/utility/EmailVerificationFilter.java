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
 * Filter that checks if the user's email is verified before allowing access to protected endpoints.
 * This filter should be placed after the JWT filter in the security filter chain.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationFilter extends OncePerRequestFilter {
    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip email verification check for public endpoints and email verification endpoints
        return path.startsWith("/v1/public/") || 
               path.startsWith("/auth/") || 
               path.equals("/public/login") || 
               path.equals("/public/sign-up");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                User user = userRepo.findByUsername(username)
                        .orElseThrow(() -> new ServletException("User not found"));

                if (!user.isEmailVerified()) {
                    log.warn("Unauthorized access attempt: User {} has not verified their email", username);
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(
                            ApiResponse.error("Please verify your email before accessing this resource", 
                                    HttpStatus.FORBIDDEN)));
                    return;
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in email verification filter: {}", e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(
                    ApiResponse.error("Internal server error during email verification check", 
                            HttpStatus.INTERNAL_SERVER_ERROR)));
        }
    }
} 