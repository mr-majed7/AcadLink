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

@Service
@Slf4j
public class VerificationCodeService {
    private static final String VERIFICATION_PREFIX = "verification:";
    private static final String EMAIL_VERIFICATION_TYPE = "email:";
    private static final long OTP_EXPIRATION = 5;
    private static final Random random = new SecureRandom();
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public VerificationCodeService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public String generateOTP() {
        int otp = 100000 + random.nextInt(900000);
        return String.format("%06d", otp);
    }

    public String generateAndStoreOTP(UUID userId, String email) {
        String otp = generateOTP();
        storeVerificationCode(userId, email, otp);
        return otp;
    }

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

    public boolean isVerificationCodeValid(UUID userId, String email, String code) {
        VerificationCodeData codeData = getVerificationCode(userId, email);
        if (codeData == null) {
            return false;
        }
        return codeData.getCode().equals(code);
    }

    public void removeVerificationCode(UUID userId, String email) {
        String key = String.format("%s%s%s:%s",
                VERIFICATION_PREFIX,
                EMAIL_VERIFICATION_TYPE,
                userId.toString(),
                email);
        redisTemplate.delete(key);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class VerificationCodeData {
        private String code;
        private long generatedAt;
        private String email;
    }
}
