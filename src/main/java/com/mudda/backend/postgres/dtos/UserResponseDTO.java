package com.mudda.backend.postgres.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserResponseDTO {
    private Long userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String countryCode;
    private String phoneNumber;
    private int roleId;
    private Instant createdAt;
}
