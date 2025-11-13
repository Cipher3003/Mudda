package com.mudda.backend.user;

public class UserMapper {

    public static User toUser(CreateUserRequest userRequest) {
        return new User(
                userRequest.userName(),
                userRequest.name(),
                userRequest.phoneNumber(),
                userRequest.dateOfBirth(),
                userRequest.email(),
                userRequest.password(),
                userRequest.profileImageUrl(),
                userRequest.roleId()
        );
    }

    public static UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(
                user.getUserId(),
                user.getUserName(),
                user.getProfileImageUrl());
    }

    public static UserDetailResponse toDetail(User user, String role) {
        return new UserDetailResponse(
                user.getUserId(),
                user.getUserName(),
                user.getName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.getRoleId(),
                role,
                user.getCreatedAt()
        );
    }
}
