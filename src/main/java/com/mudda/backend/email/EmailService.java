/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : EmailService
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.email;

public interface EmailService {

    //    TODO: make robust with retry and rate limit
    void sendVerificationEmail(String email, String token);

    void sendPasswordResetEmail(String email, String token);
}
