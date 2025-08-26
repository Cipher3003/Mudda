package com.mudda.backend.postgres.models;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
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

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // TODO: delete user if role is deleted ?
    @Column(nullable = false)
    private Long roleId;

    @ManyToMany
    @JoinTable(name = "user_locations", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "location_id"))
    private Set<Location> locations = new HashSet<>();

    // One user can have one role
    @ManyToOne
    @JoinColumn(name = "role_id") // foreign key to role
    private Role role;

}
