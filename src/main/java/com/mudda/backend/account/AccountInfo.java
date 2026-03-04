/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AccountInfo
 * Author  : Vikas Kumar
 * Created : 02-02-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.account;

import com.mudda.backend.user.MuddaUserRole;

import java.time.Instant;

public record AccountInfo(
        Long id,
        String username,
        String name,
        String email,
        String phoneNumber,
        String profileImageUrl,
        MuddaUserRole role,
        Instant createAt
) {
}
