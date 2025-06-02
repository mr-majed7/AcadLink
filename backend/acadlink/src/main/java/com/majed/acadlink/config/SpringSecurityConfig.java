package com.majed.acadlink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.majed.acadlink.utility.EmailVerificationFilter;
import com.majed.acadlink.utility.JWTFilter;

/**
 * Spring Security configuration for the application.
 * This configuration:
 * 1. Sets up JWT-based authentication
 * 2. Configures endpoint security
 * 3. Manages password encoding
 * 4. Handles authentication manager setup
 * 5. Enforces email verification for protected endpoints
 */
@Configuration
public class SpringSecurityConfig {
    private final JWTFilter jwtFilter;
    private final EmailVerificationFilter emailVerificationFilter;

    public SpringSecurityConfig(JWTFilter jwtFilter, EmailVerificationFilter emailVerificationFilter) {
        this.jwtFilter = jwtFilter;
        this.emailVerificationFilter = emailVerificationFilter;
    }

    /**
     * Configures the security filter chain for the application.
     * 
     * Security Model:
     * 1. JWT-based Authentication:
     *    - Uses JWT tokens in Authorization header
     *    - Tokens are not stored in cookies
     *    - Stateless authentication
     * 
     * 2. CSRF Protection:
     *    - CSRF protection is disabled because:
     *      a. We use JWT tokens in Authorization headers
     *      b. Tokens are not stored in cookies
     *      c. Tokens must be explicitly included in each request
     *      d. Browsers don't automatically send Authorization headers
     *      e. Our API is stateless and doesn't rely on session cookies
     *    - This is a secure approach for JWT-based APIs
     *    - CSRF attacks primarily target cookie-based authentication
     * 
     * 3. Endpoint Security:
     *    - /v1/public/**: Public access
     *    - /v1/folder/**, /v1/user/**, /v1/material/**, /v1/peers/**: Authenticated access + Email verification required
     *    - /admin/**: Admin role required
     *    - All other endpoints: Public access
     *
     * 4. Email Verification:
     *    - Required for all protected endpoints
     *    - Skip verification for public endpoints and email verification endpoints
     *    - Returns 403 Forbidden if email is not verified
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @SuppressWarnings("java:S4502") // CSRF protection is disabled as we use JWT tokens in Authorization header
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(request -> request
                        .requestMatchers("/v1/public/**").permitAll()
                        .requestMatchers(
                                "/v1/folder/**", "/v1/user/**", "v1/material/**", "v1/peers/**"
                        ).authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(emailVerificationFilter, JWTFilter.class)
                // CSRF protection is disabled as we use JWT tokens in Authorization header
                // This is safe because:
                // 1. JWT tokens are not stored in cookies
                // 2. Tokens must be explicitly included in each request
                // 3. Browsers don't automatically send Authorization headers
                // 4. Our API is stateless and doesn't rely on session cookies
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
