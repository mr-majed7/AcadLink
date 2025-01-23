package com.majed.acadlink.service;

import com.majed.acadlink.dto.UserDTO;
import com.majed.acadlink.dto.UserSignUp;
import com.majed.acadlink.entitie.User;
import com.majed.acadlink.repository.UserRepo;
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

    public UserDTO createUser(UserSignUp userData) {

        User user = new User();
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setEmail(userData.getEmail());
        user.setUsername(userData.getUserName());
        user.setPassword(passWordEncoder.encode(userData.getPassword()));

        try {
            User savedUser = userRepo.save(user);
            return new UserDTO(
                    savedUser.getId(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
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
