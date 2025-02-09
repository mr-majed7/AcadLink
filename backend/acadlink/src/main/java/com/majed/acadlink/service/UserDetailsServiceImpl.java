package com.majed.acadlink.service;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        // Determine if the input is an email or username
        User user;
        if (input.contains("@")) {
            // Input is an email
            user = userRepo.findByEmail(input)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + input));
        } else {
            // Input is a username
            user = userRepo.findByUsername(input)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + input));
        }

        // Build and return a Spring Security User object
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}
