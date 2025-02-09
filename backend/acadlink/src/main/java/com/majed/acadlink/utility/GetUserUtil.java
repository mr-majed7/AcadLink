package com.majed.acadlink.utility;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetUserUtil {

    @Autowired
    private UserRepo userRepo;

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public Optional<User> getAuthenticatedUser() {
        String username = getAuthenticatedUsername();
        return userRepo.findByUsername(username);
    }
}
