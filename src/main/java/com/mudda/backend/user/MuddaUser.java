package com.mudda.backend.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class MuddaUser implements UserDetails {

    @Id
    @SequenceGenerator(name = "users_seq", sequenceName = "users_id_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

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

    @Setter
    @Column(nullable = false)
    private boolean enabled = false;

    @Setter
    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    @Column
    private Instant lockUntil;

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

    public MuddaUser(String username, String name, String phoneNumber,
                     LocalDate dateOfBirth, String email,
                     String hashedPassword, String profileImageUrl, MuddaUserRole role) {
        this.username = username;
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

    public void recordFailedLoginAttempt(int maxAttempts, Duration lockDuration) {
        failedLoginAttempts++;

        if (failedLoginAttempts >= maxAttempts)
            this.lockUntil = Instant.now().plus(lockDuration);
    }

    public void resetLoginFailures() {
        this.failedLoginAttempts = 0;
        this.lockUntil = null;
    }

    public boolean unlockIfExpires() {
        if (lockUntil != null && this.lockUntil.isBefore(Instant.now())) {
            this.failedLoginAttempts = 0;
            this.lockUntil = null;
            return true;
        }
        return false;
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

    public void verify() {
        this.enabled = true;
    }

    //    Getter

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        return Collections.singleton(authority);
    }

    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public String getUsername() {
        return username;
    }

    //    TODO: dont know what to do with this
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked();
    }

    //    TODO: dont know what to do with this
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLocked() {
        return lockUntil != null && lockUntil.isAfter(Instant.now());
    }
}
