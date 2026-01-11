package com.mudda.backend.user;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class MuddaUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_id_seq", allocationSize = 50)
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MuddaUserRole role;

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

    // ----- Domain Constructor -----

    public MuddaUser(String userName, String name, String phoneNumber,
                     LocalDate dateOfBirth, String email,
                     String hashedPassword, String profileImageUrl, MuddaUserRole role) {
        this.userName = userName;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    // ----- Domain Behaviour -------

    public void updateDetails(String phoneNumber, String profileImageUrl) {
        if (phoneNumber == null || phoneNumber.isBlank())
            throw new IllegalArgumentException("Phone number cannot be empty");
        if (profileImageUrl == null || profileImageUrl.isBlank())
            throw new IllegalArgumentException("Profile Image Url cannot be empty");

        changePhoneNumber(phoneNumber);
        changeProfileImageUrl(profileImageUrl);
    }

//    Setter

    public void changePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void changePasswordHash(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void changeProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
