/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : RoleSeed
 * Author  : Vikas Kumar
 * Created : 28-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.role;

public record RoleSeed(
        String name
) {

    public static Role toRole(RoleSeed seed) {
        return new Role(
                seed.name()
        );
    }
}
