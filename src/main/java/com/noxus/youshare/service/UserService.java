package com.noxus.youshare.service;

import com.noxus.youshare.dto.AuthResponseDTO;
import com.noxus.youshare.dto.LoginRequestDTO;
import com.noxus.youshare.dto.RegisterRequestDTO;
import com.noxus.youshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public AuthResponseDTO register(RegisterRequestDTO user) {

        return new AuthResponseDTO();
    }

    public AuthResponseDTO login(LoginRequestDTO user) {

        return new AuthResponseDTO();
    }
}
