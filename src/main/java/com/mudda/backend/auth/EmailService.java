/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : EmailService
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

public interface EmailService {

    void sendVerificationEmail(String email, String token);
}
