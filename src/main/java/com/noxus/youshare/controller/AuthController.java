package com.noxus.youshare.controller;

import com.noxus.youshare.dto.AuthResponseDTO;
import com.noxus.youshare.dto.LoginRequestDTO;
import com.noxus.youshare.dto.RegisterRequestDTO;
import com.noxus.youshare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO newUser) {
        AuthResponseDTO user = service.register(newUser);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO user) {
        AuthResponseDTO savedUser = service.login(user);

        if (savedUser == null) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(savedUser);
    }
}
