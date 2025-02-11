package com.majed.acadlink.service;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.user.UserResponseDTO;
import com.majed.acadlink.dto.user.UserSignUpDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    private static final PasswordEncoder passWordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepo userRepo;

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
