package com.mudda.backend.postgres.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserResponseDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String profileImageUrl;
    private Long roleId;
    private Instant createdAt;
}
