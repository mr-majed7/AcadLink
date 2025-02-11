package com.majed.acadlink.service;

import com.majed.acadlink.domain.entitie.User;
import com.majed.acadlink.domain.repository.PeersRepo;
import com.majed.acadlink.domain.repository.UserRepo;
import com.majed.acadlink.dto.peers.SearchResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PeersManagementService {
    @Autowired
    private PeersRepo peersRepo;

    @Autowired
    private UserRepo userRepo;

    public List<SearchResultDTO> searchUsers(String entry) {
        List<User> userList = userRepo.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(entry, entry);

        return userList.stream().map(value -> new SearchResultDTO(value.getId(), value.getFirstName()
                        , value.getLastName(), value.getEmail(), value.getEmail(), value.getInstitute()))
                .collect(Collectors.toList());
    }

}
