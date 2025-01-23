package com.majed.acadlink.dto;

import lombok.Data;

@Data
public class UserSignUp {
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String password;
}
