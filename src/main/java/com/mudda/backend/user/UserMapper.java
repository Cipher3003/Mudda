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
                userRequest.role()
        );
    }

    public static UserSummaryResponse toSummary(MuddaUser muddaUser) {
        return new UserSummaryResponse(
                muddaUser.getUserId(),
                muddaUser.getUsername(),
                muddaUser.getProfileImageUrl());
    }

    public static UserDetailResponse toDetail(MuddaUser muddaUser) {
        return new UserDetailResponse(
                muddaUser.getUserId(),
                muddaUser.getUsername(),
                muddaUser.getName(),
                muddaUser.getPhoneNumber(),
                muddaUser.getEmail(),
                muddaUser.getProfileImageUrl(),
                muddaUser.getRole(),
                muddaUser.getCreatedAt(),
                muddaUser.isLocked(),
                muddaUser.isEnabled()
        );
    }
}
