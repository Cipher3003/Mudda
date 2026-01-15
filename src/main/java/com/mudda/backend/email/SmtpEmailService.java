/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : SmtpEmailService
 * Author  : Vikas Kumar
 * Created : 15-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    public SmtpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public void sendVerificationEmail(String email, String token) {
        String link = "http://localhost:8080/auth/verify-email/confirm?verifyToken=%s".formatted(token);
        sendHtmlEmail(
                email,
                "Verify your email",
                buildVerifyEmailHtml(link)
        );
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String email, String token) {
        String link = "http://localhost:8080/auth/reset-password?verifyToken=%s".formatted(token);
        sendHtmlEmail(
                email,
                "Reset your password",
                buildResetPasswordEmailHtml(link)
        );
    }

    @Retryable(
            retryFor = MailException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000)
    )
    public void sendHtmlEmail(String email, String subject, String html) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new MailSendException("Failed to construct or send email", e);
        }
    }

    @Recover
    public void recover(MailException e, String email, String subject, String html) {
        log.error("Failed to send email to {} with subject '{}' after retries", email, subject, e);
    }

    private String buildVerifyEmailHtml(String link) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="margin:0;padding:0;background:#f5f7fa;">
                <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0"
                                   style="background:#ffffff;padding:30px;border-radius:8px;font-family:Arial,sans-serif;">
                                <tr>
                                    <td align="center">
                                        <h2 style="color:#111827;">Verify your email</h2>
                
                                        <p style="color:#374151;font-size:14px;">
                                            Thanks for creating an account.
                                        </p>
                
                                        <table cellpadding="0" cellspacing="0" style="margin:30px auto;">
                                            <tr>
                                                <td bgcolor="#2563eb" style="border-radius:6px;">
                                                    <a href="%s"
                                                       style="
                                                         display:inline-block;
                                                         padding:12px 18px;
                                                         font-family:Arial,sans-serif;
                                                         font-size:14px;
                                                         color:#ffffff;
                                                         text-decoration:none;
                                                         font-weight:bold;
                                                       ">
                                                        Verify Email
                                                    </a>
                                                </td>
                                            </tr>
                                        </table>
                
                                        <p style="color:#6b7280;font-size:12px;">
                                            If you didn’t create this account, ignore this email.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
                </body>
                </html>
                """.formatted(link);
    }

    private String buildResetPasswordEmailHtml(String link) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="margin:0;padding:0;background:#f5f7fa;">
                <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                        <td align="center">
                            <table width="600" cellpadding="0" cellspacing="0"
                                   style="background:#ffffff;padding:30px;border-radius:8px;font-family:Arial,sans-serif;">
                                <tr>
                                    <td align="center">
                
                                        <h2 style="color:#111827;">Reset your password</h2>
                
                                        <p style="color:#374151;font-size:14px;">
                                            We received a request to reset your password.
                                        </p>
                
                                        <p style="color:#374151;font-size:14px;">
                                            Click the button below to choose a new password.
                                        </p>
                
                                        <table cellpadding="0" cellspacing="0" style="margin:30px auto;">
                                            <tr>
                                                <td bgcolor="#dc2626" style="border-radius:6px;">
                                                    <a href="%s"
                                                       style="
                                                         display:inline-block;
                                                         padding:12px 18px;
                                                         font-family:Arial,sans-serif;
                                                         font-size:14px;
                                                         color:#ffffff;
                                                         text-decoration:none;
                                                         font-weight:bold;
                                                       ">
                                                        Reset Password
                                                    </a>
                                                </td>
                                            </tr>
                                        </table>
                
                                        <p style="color:#6b7280;font-size:12px;">
                                            If you didn’t request a password reset, you can safely ignore this email.
                                            Your password will remain unchanged.
                                        </p>
                
                                        <p style="color:#9ca3af;font-size:11px;margin-top:20px;">
                                            This link will expire for security reasons.
                                        </p>
                
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
                </body>
                </html>
                """.formatted(link);
    }


}
