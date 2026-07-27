/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : SmtpEmailService
 * Author  : Vikas Kumar
 * Created : 15-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.email;

import com.mudda.backend.AppProperties;
import com.mudda.backend.exceptions.TemplateLoadException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@Profile({"prod", "stage"})
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailConfig emailConfig;
    private final AppProperties appProperties;

    public static final String VERIFICATION_EMAIL_HTML = "templaes/verify-email.html";
    public static final String PASSWORD_RESET_EMAIL_HTML = "templates/reset-password-email.html";

    public SmtpEmailService(
            JavaMailSender mailSender,
            EmailConfig emailConfig,
            AppProperties appProperties
    ) {
        this.mailSender = mailSender;
        this.emailConfig = emailConfig;
        this.appProperties = appProperties;
    }

    @Override
    public void sendVerificationEmail(String email, String token) {
        String link = "%s%s?verifyToken=%s".formatted(
                appProperties.getFrontendBaseUrl(), emailConfig.getVerifyPath(), token);

        sendHtmlEmail(
                email,
                "Verify your email",
                buildEmailFromResource(VERIFICATION_EMAIL_HTML, link)
        );
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {
        String link = "%s%s?verifyToken=%s".formatted(
                appProperties.getFrontendBaseUrl(), emailConfig.getResetPath(), token);

        sendHtmlEmail(
                email,
                "Reset your password",
                buildEmailFromResource(PASSWORD_RESET_EMAIL_HTML, link)
        );
    }

    @Retryable(
            retryFor = MailException.class,
            maxAttemptsExpression = "#{@appProperties.email.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "#{@appProperties.email.retry.backOffMs}")
    )
    public void sendHtmlEmail(String email, String subject, String html) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage, true, StandardCharsets.UTF_8.name());

            helper.setFrom(appProperties.getEmail().getFrom());
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(mimeMessage);

            log.info("Sent email to={} with subject={}", email, subject);

        } catch (MessagingException e) {
            throw new MailSendException("Failed to construct or send email", e);
        }
    }

    @Recover
    public void recover(MailException e, String email, String subject, String html) {
        log.error("Failed to send email to {} with subject '{}' after retries", email, subject, e);
    }

    private String buildEmailFromResource(String resourcePath, String link) {
        try {
            Resource resource = new ClassPathResource(resourcePath);
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template.replace("${link}", link);
        } catch (IOException e) {
            throw new TemplateLoadException();
        }
    }

}
