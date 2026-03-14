/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UserSeed
 * Author  : Vikas Kumar
 * Created : 28-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.user.dto;


import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.MuddaUserRole;

import java.time.LocalDate;

public record UserSeed(
        String username,
        String name,
        String email,
        String dateOfBirth,
        String phoneNumber,
        String password,
        MuddaUserRole role,
        String profileImageUrl
) {

    public static MuddaUser toUser(UserSeed seed) {
        return new MuddaUser(
                seed.username(),
                seed.name(),
                seed.phoneNumber(),
                LocalDate.parse(seed.dateOfBirth()),
                seed.email(),
                seed.password(),
                seed.profileImageUrl(),
                seed.role()
        );
    }
}
