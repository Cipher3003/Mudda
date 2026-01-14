/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : SmtpEmailService
 * Author  : Vikas Kumar
 * Created : 15-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    public SmtpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String email, String token) {
        String link = "localhost:8080/auth/verify-email?verifyToken=%s".formatted(token);
        sendEmail(
                email,
                "Verify your email",
                "Click the link to verify your email:\n" + link
        );
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {
        String link = "localhost:8080/auth/reset-password?verifyToken=%s".formatted(token);
        sendEmail(
                email,
                "Reset your password",
                "Click the link to reset your password:\n" + link
        );
    }

    private void sendEmail(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
