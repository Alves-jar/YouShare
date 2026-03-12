package com.noxus.youshare.service;

import com.noxus.youshare.dto.AuthResponseDTO;
import com.noxus.youshare.dto.LoginRequestDTO;
import com.noxus.youshare.dto.RegisterRequestDTO;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.entity.enums.UserRole;
import com.noxus.youshare.exception.UserNotFoundException;
import com.noxus.youshare.repository.UserRepository;
import com.noxus.youshare.security.JwtTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthResponseDTO register(RegisterRequestDTO newUser) {
        UserRole role = newUser.role().equals("editor") ? UserRole.EDITOR : UserRole.CREATOR;
        User user = new User(
            null,
            newUser.username(),
            newUser.email(),
            passwordEncoder.encode(newUser.password()),
            role
        );

        repository.save(user);

        return new AuthResponseDTO(
            user.getEmail(),
            jwtTokenService.generateToken(user)
        );
    }

    public AuthResponseDTO login(LoginRequestDTO user) {
        User registeredUser = repository.findByEmail(user.email())
                .orElseThrow(() -> new UserNotFoundException("User not found.")
            );
        if (passwordEncoder.matches(user.password(), registeredUser.getPassword())) {
            return new AuthResponseDTO(
                registeredUser.getEmail(),
                jwtTokenService.generateToken(registeredUser)
            );
        }
        throw new RuntimeException("Invalid credentials");
    }
}
