/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UserSeed
 * Author  : Vikas Kumar
 * Created : 28-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.user;


import java.time.LocalDate;

public record UserSeed(
        String userName,
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
                seed.userName(),
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
