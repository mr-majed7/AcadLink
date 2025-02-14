package com.majed.acadlink.utility;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.majed.acadlink.domain.entitie.User;

@Component
public class AuthorizationCheck {
    private final GetUserUtil getUserUtil;

    public AuthorizationCheck(GetUserUtil getUserUtil) {
        this.getUserUtil = getUserUtil;
    }

    public boolean checkAuthorization(UUID id) {
        Optional<User> current = getUserUtil.getAuthenticatedUser();

        return current.isPresent() && current.get().getId().equals(id);
    }

}
