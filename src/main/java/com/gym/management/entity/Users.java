package com.gym.management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = true)
    private String username;

    @Column(nullable = true)
    private String passwordHash;

    @Column(nullable = false)
    private String role; // admin or member

    private String name;

    @Column(unique = true)
    private String email;
}

