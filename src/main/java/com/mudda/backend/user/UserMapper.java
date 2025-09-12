package com.mudda.backend.user;

public class UserMapper {

    public static User toEntity(CreateUserRequest userRequest) {
        User user = new User();
        user.setUserName(userRequest.userName());
        user.setName(userRequest.name());
        user.setEmail(userRequest.email());
        user.setDateOfBirth(userRequest.dateOfBirth());
        user.setPhoneNumber(userRequest.phoneNumber());
        user.setHashedPassword(userRequest.hashedPassword());
        user.setRoleId(userRequest.roleId());
        user.setProfileImageUrl(userRequest.profileImageUrl());
        return user;
    }
}
