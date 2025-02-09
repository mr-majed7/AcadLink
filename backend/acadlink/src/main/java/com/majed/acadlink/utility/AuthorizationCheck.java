package com.majed.acadlink.utility;

import com.majed.acadlink.domain.entitie.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuthorizationCheck {
    @Autowired
    private GetUserUtil getUserUtil;

    public boolean checkAuthorization(UUID id) {
        Optional<User> current = getUserUtil.getAuthenticatedUser();

        return current.isPresent() && current.get().getId().equals(id);
    }

}
