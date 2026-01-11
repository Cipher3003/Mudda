package com.mudda.backend.user;

public class UserMapper {

    public static MuddaUser toUser(CreateUserRequest userRequest) {
        return new MuddaUser(
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

    public static UserSummaryResponse toSummary(MuddaUser muddaUser) {
        return new UserSummaryResponse(
                muddaUser.getUserId(),
                muddaUser.getUserName(),
                muddaUser.getProfileImageUrl());
    }

    public static UserDetailResponse toDetail(MuddaUser muddaUser, String role) {
        return new UserDetailResponse(
                muddaUser.getUserId(),
                muddaUser.getUserName(),
                muddaUser.getName(),
                muddaUser.getPhoneNumber(),
                muddaUser.getEmail(),
                muddaUser.getProfileImageUrl(),
                muddaUser.getRoleId(),
                role,
                muddaUser.getCreatedAt()
        );
    }
}
