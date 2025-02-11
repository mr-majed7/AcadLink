package com.majed.acadlink.dto.user;

import lombok.Data;

@Data
public class UserSignUpDTO {
    private String firstName;
    private String lastName;
    private String institute;
    private String email;
    private String userName;
    private String password;
}
