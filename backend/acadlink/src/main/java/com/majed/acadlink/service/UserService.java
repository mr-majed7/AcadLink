package com.majed.acadlink.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.dto.user.UserSignUpDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
    private static final PasswordEncoder passWordEncoder = new BCryptPasswordEncoder();

    private final UserRepo userRepo;
    
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserResponseDTO createUser(UserSignUpDTO userData) {

        User user = new User();
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setInstitute(userData.getInstitute());
        user.setEmail(userData.getEmail());
        user.setUsername(userData.getUserName());
        user.setPassword(passWordEncoder.encode(userData.getPassword()));

        try {
            User savedUser = userRepo.save(user);
            return new UserResponseDTO(
                    savedUser.getId(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    savedUser.getInstitute(),
                    savedUser.getEmail(),
                    savedUser.getUsername(),
                    savedUser.getCreatedAt()
            );
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }
    }
}
