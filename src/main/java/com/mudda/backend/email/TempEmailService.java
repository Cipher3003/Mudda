/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : TempEmailService
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.email;

import org.springframework.stereotype.Service;

@Service
public class TempEmailService implements EmailService {
    @Override
    public void sendVerificationEmail(String email, String token) {
        String verificationLink = "localhost:8080/auth/verify-email?verifyToken=%s".formatted(token);
        System.out.printf("Sending verification Link: {%s} to Email: {%s}%n", verificationLink, email);
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {
        String verificationLink = "localhost:8080/auth/reset-password?verifyToken=%s".formatted(token);
        System.out.printf("Sending verification Link for password reset: {%s} to Email: {%s}%n", verificationLink, email);
    }
}
