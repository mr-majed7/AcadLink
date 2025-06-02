package com.majed.acadlink.utility;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.majed.acadlink.domain.entity.User;
import com.majed.acadlink.domain.repository.UserRepo;

@Component
public class GetUserUtil {
    private final UserRepo userRepo;

    public GetUserUtil(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public Optional<User> getAuthenticatedUser() {
        String username = getAuthenticatedUsername();
        return userRepo.findByUsername(username);
    }
}
