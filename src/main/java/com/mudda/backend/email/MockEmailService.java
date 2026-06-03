/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MockEmailService
 * Author  : Vikas Kumar
 * Created : 6/3/2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockEmailService implements EmailService {

    @Override
    public void sendVerificationEmail(String email, String token) {
        log.info("Sending verification email to {} with token {}", email, token);
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {
        log.info("Sending password reset email to {} with token {}", email, token);
    }
}
