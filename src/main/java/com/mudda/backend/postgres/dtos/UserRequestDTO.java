package com.mudda.backend.postgres.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String hashedPassword;
    private Long roleId;
    private String profileImageUrl;
}
