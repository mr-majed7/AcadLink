package com.majed.acadlink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private LocalDate createdAt;


}
