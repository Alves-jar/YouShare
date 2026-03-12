package com.noxus.youshare.entity;

import com.noxus.youshare.entity.enums.UserRole;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String email;
    private String password;
    private UserRole role;
}
