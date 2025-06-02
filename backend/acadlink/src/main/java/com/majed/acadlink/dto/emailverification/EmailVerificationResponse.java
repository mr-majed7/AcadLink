package com.majed.acadlink.dto.emailverification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailVerificationResponse {
    private boolean verified;
    private String message;
}
