package com.mudda.backend.community;

/**
 * Role a user holds within a specific Community.
 * Separate from the global {@link com.mudda.backend.user.MuddaUserRole}.
 */
public enum CommunityRole {
    RESIDENT,
    MODERATOR,
    ADMIN
}
