package com.majed.acadlink.dto.peers;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PeerInfoDTO {
    private UUID reqId;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String institute;
    private String userName;

}
