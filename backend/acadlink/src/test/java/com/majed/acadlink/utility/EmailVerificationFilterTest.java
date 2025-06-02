package com.majed.acadlink.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.majed.acadlink.domain.entity.User;
import com.majed.acadlink.domain.repository.UserRepo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class EmailVerificationFilterTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EmailVerificationFilter filter;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Mock
    private SecurityContext securityContext;

    private static final List<GrantedAuthority> USER_AUTHORITIES = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldNotFilter_PublicPath() {
        request.setRequestURI("/v1/public/test");
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilter_AuthPath() {
        request.setRequestURI("/auth/verify-email");
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilter_LoginPath() {
        request.setRequestURI("/public/login");
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilter_SignupPath() {
        request.setRequestURI("/public/sign-up");
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldFilter_ProtectedPath() {
        request.setRequestURI("/v1/user/profile");
        assertFalse(filter.shouldNotFilter(request));
    }

    @Test
    void doFilterInternal_NotAuthenticated_ContinuesChain() throws ServletException, IOException {
        // Arrange
        request.setRequestURI("/v1/user/profile");
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void doFilterInternal_AuthenticatedVerifiedUser_ContinuesChain() throws ServletException, IOException {
        // Arrange
        request.setRequestURI("/v1/user/profile");
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setEmailVerified(true);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, USER_AUTHORITIES);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
        verify(userRepo).findByUsername(username);
    }

    @Test
    void doFilterInternal_AuthenticatedUnverifiedUser_ReturnsForbidden() throws Exception {
        // Arrange
        request.setRequestURI("/v1/user/profile");
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setEmailVerified(false);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, USER_AUTHORITIES);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"error\":\"test error\"}");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertEquals(403, response.getStatus());
        verify(filterChain, never()).doFilter(any(), any());
        verify(userRepo).findByUsername(username);
        verify(objectMapper).writeValueAsString(any());
    }

    @Test
    void doFilterInternal_UserNotFound_ReturnsInternalServerError() throws Exception {
        // Arrange
        request.setRequestURI("/v1/user/profile");
        String username = "testuser";

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, USER_AUTHORITIES);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"error\":\"test error\"}");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertEquals(500, response.getStatus());
        verify(filterChain, never()).doFilter(any(), any());
        verify(userRepo).findByUsername(username);
        verify(objectMapper).writeValueAsString(any());
    }

    @Test
    void doFilterInternal_ExceptionDuringProcessing_ReturnsInternalServerError() throws Exception {
        // Arrange
        request.setRequestURI("/v1/user/profile");
        String username = "testuser";

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, USER_AUTHORITIES);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(userRepo.findByUsername(username)).thenThrow(new RuntimeException("DB error"));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"error\":\"test error\"}");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertEquals(500, response.getStatus());
        verify(filterChain, never()).doFilter(any(), any());
        verify(userRepo).findByUsername(username);
        verify(objectMapper).writeValueAsString(any());
    }
}