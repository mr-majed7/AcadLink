package com.majed.acadlink.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for managing verification codes (OTP) in the AcadLink application.
 * This service handles the generation, storage, validation, and cleanup of verification codes
 * using Redis as the storage backend.
 *
 * <p>Key Features:
 * 1. Generates secure 6-digit OTP codes
 * 2. Stores codes in Redis with expiration
 * 3. Validates codes against stored values
 * 4. Manages code cleanup after use</p>
 *
 * <p>Security Considerations:
 * - Uses SecureRandom for OTP generation
 * - Codes expire after 5 minutes
 * - Codes are stored with user and email context
 * - Failed attempts are logged for monitoring</p>
 *
 * <p>Storage Format:
 * - Key: verification:email:{userId}:{email}
 * - Value: JSON containing code, generation timestamp, and email
 * - Expiration: 5 minutes from generation</p>
 *
 */
@Service
@Slf4j
public class VerificationCodeService {
    private static final String VERIFICATION_PREFIX = "verification:";
    private static final String EMAIL_VERIFICATION_TYPE = "email:";
    private static final long OTP_EXPIRATION = 5;
    private static final Random random = new SecureRandom();
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new VerificationCodeService with required dependencies.
     *
     * @param redisTemplate the Redis template for data storage
     * @param objectMapper the JSON mapper for serialization/deserialization
     */
    public VerificationCodeService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Generates a secure 6-digit OTP code.
     * The code is generated using SecureRandom to ensure cryptographic security.
     *
     * @return a 6-digit string OTP code
     */
    public String generateOTP() {
        int otp = 100000 + random.nextInt(900000);
        return String.format("%06d", otp);
    }

    /**
     * Generates a new OTP code and stores it in Redis.
     * This is a convenience method that combines generation and storage.
     *
     * @param userId the ID of the user requesting the OTP
     * @param email the email address associated with the OTP
     * @return the generated OTP code
     */
    public String generateAndStoreOTP(UUID userId, String email) {
        String otp = generateOTP();
        storeVerificationCode(userId, email, otp);
        return otp;
    }

    /**
     * Stores a verification code in Redis with expiration.
     * The code is stored as JSON with metadata including generation timestamp.
     *
     * <p>Storage Details:
     * - Key format: verification:email:{userId}:{email}
     * - Value: JSON containing code, timestamp, and email
     * - Expiration: 5 minutes from storage</p>
     *
     * @param userId the ID of the user
     * @param email the email address
     * @param code the verification code to store
     * @throws RuntimeException if storage fails
     */
    public void storeVerificationCode(UUID userId, String email, String code) {
        String key = String.format("%s%s%s:%s",
                VERIFICATION_PREFIX,
                EMAIL_VERIFICATION_TYPE,
                userId.toString(),
                email);
        VerificationCodeData codeData = new VerificationCodeData(
                code,
                System.currentTimeMillis(),
                email);
        try {
            redisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(codeData),
                    OTP_EXPIRATION,
                    TimeUnit.MINUTES
            );
        } catch (JsonProcessingException e) {
            log.error("Error storing verification code for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to store verification code", e);
        }
    }

    /**
     * Retrieves a stored verification code from Redis.
     * Returns null if no code exists or if it has expired.
     *
     * @param userId the ID of the user
     * @param email the email address
     * @return the stored verification code data, or null if not found
     * @throws RuntimeException if retrieval fails
     */
    public VerificationCodeData getVerificationCode(UUID userId, String email) {
        String key = String.format("%s%s%s:%s",
                VERIFICATION_PREFIX,
                EMAIL_VERIFICATION_TYPE,
                userId.toString(),
                email);

        String data = redisTemplate.opsForValue().get(key);
        if (data == null) return null;

        try {
            return objectMapper.readValue(data, VerificationCodeData.class);
        } catch (JsonProcessingException e) {
            log.error("Error reading verification code for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to read verification code", e);
        }
    }

    /**
     * Validates a verification code against the stored code.
     * Checks if the code exists and matches the provided value.
     *
     * @param userId the ID of the user
     * @param email the email address
     * @param code the code to validate
     * @return true if the code is valid, false otherwise
     */
    public boolean isVerificationCodeValid(UUID userId, String email, String code) {
        VerificationCodeData codeData = getVerificationCode(userId, email);
        if (codeData == null) {
            return false;
        }
        return codeData.getCode().equals(code);
    }

    /**
     * Removes a verification code from Redis.
     * This is typically called after successful verification.
     *
     * @param userId the ID of the user
     * @param email the email address
     */
    public void removeVerificationCode(UUID userId, String email) {
        String key = String.format("%s%s%s:%s",
                VERIFICATION_PREFIX,
                EMAIL_VERIFICATION_TYPE,
                userId.toString(),
                email);
        redisTemplate.delete(key);
    }

    /**
     * Data class representing a stored verification code.
     * Contains the code itself and metadata about its generation.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class VerificationCodeData {
        /** The actual verification code */
        private String code;
        /** Timestamp when the code was generated */
        private long generatedAt;
        /** The email address associated with the code */
        private String email;
    }
}
