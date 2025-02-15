package com.majed.acadlink.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling JWT operations such as generating and validating tokens.
 */
@Component
public class JWTUtil {
    @Value("${spring.custom.security.jwtsecret}")
    private String secretKey;

    /**
     * Retrieves the signing key used for JWT operations.
     *
     * @return the secret key used for signing JWTs
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Extracts the username from the provided JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    /**
     * Extracts the expiration date from the provided JWT token.
     *
     * @param token the JWT token
     * @return the expiration date of the token
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Extracts all claims from the provided JWT token.
     *
     * @param token the JWT token
     * @return the claims extracted from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks if the provided JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a new JWT token for the provided username.
     *
     * @param username the username for which the token is generated
     * @return the generated JWT token
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Creates a new JWT token with the provided claims and subject.
     *
     * @param claims  the claims to be included in the token
     * @param subject the subject of the token
     * @return the created JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ", "JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiration time
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates the provided JWT token.
     *
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}
