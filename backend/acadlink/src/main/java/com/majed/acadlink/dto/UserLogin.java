package com.majed.acadlink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLogin {
    private String usernameorEmail;
    private String password;
}
