/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UserRegisteredEvent
 * Author  : Vikas Kumar
 * Created : 7/10/2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.account;

public record UserRegisteredEvent(
        String email,
        String token
) {
}
