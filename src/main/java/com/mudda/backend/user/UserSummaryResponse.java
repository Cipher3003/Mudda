package com.mudda.backend.user;

public record UserSummaryResponse(
        Long userId,
        String userName,
        String profileImageUrl
) {

    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
                user.getUserId(),
                user.getUserName(),
                user.getProfileImageUrl());
    }
}
