package com.majed.acadlink.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String institute;
    private String email;
    private String userName;
    private LocalDate createdAt;


}
