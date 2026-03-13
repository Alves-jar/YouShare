package com.noxus.youshare.controller;

import com.noxus.youshare.dto.user.UserRequestDTO;
import com.noxus.youshare.dto.user.UserResponseDTO;
import com.noxus.youshare.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Tag(name = "Users", description = "Endpoints for user management")
public class UserController {

    private final UserService service;

    @Operation(summary = "Create new user", description = "Creates a new user in the system")
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

    @Operation(summary = "List all users", description = "Returns a list of all registered users")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<UserResponseDTO> users = service.findAll();

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID", description = "Returns details of a specific user")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(
            @Parameter(description = "User ID", required = true) @PathVariable UUID id) {
        UserResponseDTO user= service.findById(id);

        return ResponseEntity.ok(user);
    }
}
