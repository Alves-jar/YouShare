package com.noxus.youshare.service;

import com.noxus.youshare.dto.auth.AuthResponseDTO;
import com.noxus.youshare.dto.auth.LoginRequestDTO;
import com.noxus.youshare.dto.auth.RegisterRequestDTO;
import com.noxus.youshare.dto.user.UserRequestDTO;
import com.noxus.youshare.dto.user.UserResponseDTO;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.entity.enums.UserRole;
import com.noxus.youshare.exception.UserNotFoundException;
import com.noxus.youshare.repository.UserRepository;
import com.noxus.youshare.security.JwtTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthResponseDTO register(RegisterRequestDTO newUser) {
        User user = new User();
        user.setId(null);
        user.setUsername(newUser.username());
        user.setEmail(newUser.email());
        user.setPassword(passwordEncoder.encode(newUser.password()));
        user.setRole(UserRole.fromString(newUser.role()));
        user.setCreatedAt(null);
        user.setProjects(null);
        user.setMemberships(null);

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

    public UserResponseDTO create(UserRequestDTO newUser) {
        User user = new User();
        user.setId(null);
        user.setUsername(newUser.username());
        user.setEmail(newUser.email());
        user.setPassword(passwordEncoder.encode(newUser.password()));
        user.setRole(UserRole.fromString(newUser.role()));
        user.setCreatedAt(null);
        user.setProjects(null);
        user.setMemberships(null);

        User savedUser = repository.save(user);

        return new UserResponseDTO(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getRole(),
            savedUser.getCreatedAt()
        );
    }

    public List<UserResponseDTO> findAll() {
        List<User> registeredUsers = repository.findAll();
        List<UserResponseDTO> users = new ArrayList<>();

        registeredUsers.forEach(u ->
            users.add(new UserResponseDTO(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getRole(),
                u.getCreatedAt()
            ))
        );

        return users;
    }

    public UserResponseDTO findById(UUID id) {
        User registeredUser = repository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        return new UserResponseDTO(
            registeredUser.getId(),
            registeredUser.getUsername(),
            registeredUser.getEmail(),
            registeredUser.getRole(),
            registeredUser.getCreatedAt()
        );
    }
}
