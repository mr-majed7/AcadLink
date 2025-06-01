package com.majed.acadlink.utility;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class JWTFilterTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private JWTFilter jwtFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;
    private UserDetails userDetails;
    private static final String TEST_TOKEN = "test.jwt.token";
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
        userDetails = mock(UserDetails.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ValidToken_Success() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Bearer " + TEST_TOKEN);
        when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USERNAME);
        when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
        when(jwtUtil.validateToken(TEST_TOKEN)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtil, times(1)).extractUsername(TEST_TOKEN);
        verify(userDetailsService, times(1)).loadUserByUsername(TEST_USERNAME);
        verify(jwtUtil, times(1)).validateToken(TEST_TOKEN);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NoToken_Success() throws ServletException, IOException {
        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtil, never()).extractUsername(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).validateToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidTokenFormat_Success() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Invalid " + TEST_TOKEN);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtil, never()).extractUsername(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).validateToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken_Success() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Bearer " + TEST_TOKEN);
        when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USERNAME);
        when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
        when(jwtUtil.validateToken(TEST_TOKEN)).thenReturn(false);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtil, times(1)).extractUsername(TEST_TOKEN);
        verify(userDetailsService, times(1)).loadUserByUsername(TEST_USERNAME);
        verify(jwtUtil, times(1)).validateToken(TEST_TOKEN);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_UserNotFound_Success() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Bearer " + TEST_TOKEN);
        when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USERNAME);
        when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(null);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtil, times(1)).extractUsername(TEST_TOKEN);
        verify(userDetailsService, times(1)).loadUserByUsername(TEST_USERNAME);
        verify(filterChain, times(1)).doFilter(request, response);
    }
} 