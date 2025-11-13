package com.mudda.backend.user;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String hashedPassword;

    @Column
    private String profileImageUrl;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

//    ----- Domain Constructor -----

    public User(String userName, String name, String phoneNumber,
                LocalDate dateOfBirth, String email,
                String hashedPassword, String profileImageUrl, Long roleId) {
        this.userName = userName;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.profileImageUrl = profileImageUrl;
        this.roleId = roleId;
    }

//    ----- Domain Behaviour -------

    public void updateDetails(String phoneNumber, String profileImageUrl) {
        if (phoneNumber == null || phoneNumber.isBlank())
            throw new IllegalArgumentException("Phone number cannot be empty");
        if (profileImageUrl == null || profileImageUrl.isBlank())
            throw new IllegalArgumentException("Profile Image Url cannot be empty");

        setPhoneNumber(phoneNumber);
        setProfileImageUrl(profileImageUrl);
    }

}
