package com.majed.acadlink.dto.peers;

import com.majed.acadlink.enums.PeerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SearchResultDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String institute;
    private PeerStatus peerStatus;
}
