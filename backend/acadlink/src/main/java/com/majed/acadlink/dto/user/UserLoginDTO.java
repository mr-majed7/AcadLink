package com.majed.acadlink.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginDTO {
    private String usernameorEmail;
    private String password;
}
