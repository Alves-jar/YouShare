package com.noxus.youshare.controller;

import com.noxus.youshare.dto.auth.AuthResponseDTO;
import com.noxus.youshare.dto.auth.LoginRequestDTO;
import com.noxus.youshare.dto.auth.RegisterRequestDTO;
import com.noxus.youshare.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    @Autowired
    private UserService service;

    @Operation(summary = "Register new user", description = "Creates a new user account in the system")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO newUser) {
        AuthResponseDTO user = service.register(newUser);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO user) {
        AuthResponseDTO savedUser = service.login(user);

        if (savedUser == null) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(savedUser);
    }
}
