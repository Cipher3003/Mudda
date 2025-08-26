package com.mudda.backend.postgres.models;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    private String email;

    // @Column(unique = true)
    // private String countryCode;

    @Column(nullable = false)
    private String hashedPassword;

    @Column(nullable = true)
    private String profileImageUrl;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
