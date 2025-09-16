package com.mudda.backend.user;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateUserRequest(
        @NotBlank String userName,
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull @Past LocalDate dateOfBirth,
        @NotBlank
        @Pattern(regexp = "^\\+?[0-9]{10,15}$",
                message = "Phone number must be 10-15 digits and can start with +")
        String phoneNumber,
        @NotBlank String password,
        @NotNull @Positive Long roleId,
        String profileImageUrl
) {
        public User toEntity(){
                User user = new User();
                user.setUserName(this.userName);
                user.setName(this.name);
                user.setEmail(this.email);
                user.setDateOfBirth(this.dateOfBirth);
                user.setPhoneNumber(this.phoneNumber);
                user.setHashedPassword(this.password);
                user.setRoleId(this.roleId);
                user.setProfileImageUrl(this.profileImageUrl);
                return user;
        }
}
