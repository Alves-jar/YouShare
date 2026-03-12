package com.noxus.youshare.controller;

import com.noxus.youshare.dto.user.UserRequestDTO;
import com.noxus.youshare.dto.user.UserResponseDTO;
import com.noxus.youshare.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserRequestDTO newUser) {
        UserResponseDTO user = service.create(newUser);

        URI uri = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(user.id())
            .toUri();

        return ResponseEntity.created(uri).body(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<UserResponseDTO> users = service.findAll();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<UserResponseDTO> findById(@PathVariable UUID id) {
        UserResponseDTO user= service.findById(id);

        return ResponseEntity.ok(user);
    }
}
