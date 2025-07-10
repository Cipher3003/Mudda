package com.mudda.backend.postgres.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String countryCode;
    private String phoneNumber;
    private String hashedPassword;
    private int roleId;
}
