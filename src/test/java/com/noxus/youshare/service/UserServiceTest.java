package com.noxus.youshare.service;

import com.noxus.youshare.dto.user.UserRequestDTO;
import com.noxus.youshare.dto.user.UserResponseDTO;
import com.noxus.youshare.entity.User;
import com.noxus.youshare.entity.enums.UserRole;
import com.noxus.youshare.repository.UserRepository;
import com.noxus.youshare.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedpassword");
        user.setRole(UserRole.CREATOR);
        user.setCreatedAt(new Date());
    }

    @Test
    void findAll_ShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).username());
    }

    @Test
    void findById_ShouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.findById(userId);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("testuser", result.username());
    }

    @Test
    void create_ShouldReturnCreatedUser() {
        UserRequestDTO request = new UserRequestDTO("newuser", "new@example.com", "password", "CREATOR");
        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.create(request);

        assertNotNull(result);
        assertEquals("testuser", result.username());
    }
}
