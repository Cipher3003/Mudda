/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AccountVerificationService
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.account;

import com.mudda.backend.AppProperties;
import com.mudda.backend.email.EmailService;
import com.mudda.backend.exceptions.UserAlreadyExistsException;
import com.mudda.backend.token.device.DeviceTokenService;
import com.mudda.backend.token.refresh.RefreshTokenService;
import com.mudda.backend.token.verification.TokenType;
import com.mudda.backend.token.verification.VerificationToken;
import com.mudda.backend.token.verification.VerificationTokenService;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserService;
import com.mudda.backend.user.dto.CreateUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
public class AccountService {

    private final UserService userService;
    private final VerificationTokenService tokenService;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;
    private final AppProperties appProperties;
    private final DeviceTokenService deviceTokenService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public AccountService(
            UserService userService,
            VerificationTokenService tokenService,
            EmailService emailService,
            RefreshTokenService refreshTokenService,
            AppProperties appProperties,
            DeviceTokenService deviceTokenService,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.refreshTokenService = refreshTokenService;
        this.appProperties = appProperties;
        this.deviceTokenService = deviceTokenService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    //region Commands (Write Operations)

    @Transactional
    public void register(CreateUserRequest createUserRequest) {
        log.info("Registering new user account");
        Optional<MuddaUser> existingUser = userService.findByEmail(createUserRequest.email());

        if (existingUser.isPresent() && existingUser.get().isEnabled()) {
            log.info("User with email {} already exists and verified", createUserRequest.email());
            throw new UserAlreadyExistsException();
        }

        MuddaUser user = existingUser.orElseGet(() -> {
            log.trace("Creating new user account");
            return userService.createUser(createUserRequest);
        });

        sendVerification(user);
    }

    @Transactional
    public void resendEmailVerificationLink(String email) {
        log.info("Resending email verification link to {}", email);
        MuddaUser user = userService.findByEmail(email).orElse(null);
        if (user == null) return;

        if (user.isEnabled()) {
            log.info("User with email {} already verified", email);
            return;
        }

        if (tokenService.recentTokenExists(user.getUserId(), TokenType.EMAIL_VERIFY, resendCooldown())) {
            log.trace("Skipping verification resend due to cooldown");
            return;
        }

        sendVerification(user);
    }

    @Transactional
    public void verifyEmail(String verifyToken) {
        log.info("Verifying email verification token {}", verifyToken);
        VerificationToken token = tokenService.consumeToken(verifyToken, TokenType.EMAIL_VERIFY);

        userService.verifyUser(token.getUserId());
    }

    @Transactional
    public void requestPasswordReset(String email) {
        log.info("Generating password reset link for user {}", email);
        MuddaUser user = userService.findByEmail(email).orElse(null);
        if (user == null) return;

        if (tokenService.recentTokenExists(user.getUserId(), TokenType.PASSWORD_RESET, resendCooldown()))
            return;

        tokenService.invalidateUnusedTokens(user.getUserId(), TokenType.PASSWORD_RESET);
        VerificationToken token = tokenService.generateToken(TokenType.PASSWORD_RESET, user.getUserId());
        applicationEventPublisher.publishEvent(new PasswordResetRequestedEvent(user.getEmail(), token.getToken()));
        log.debug("Published PasswordResetRequestedEvent for user: {}", user.getUsername());
    }

    @Transactional
    public void resetPassword(String verifyToken, String newPassword) {
        VerificationToken token = tokenService.consumeToken(verifyToken, TokenType.PASSWORD_RESET);

        Long userId = token.getUserId();
        log.info("Resetting password for user with ID:{}", userId);

        userService.updatePassword(userId, newPassword);

        refreshTokenService.revokeAllByUserId(userId);
        tokenService.deleteAllTokensByUserId(userId);
    }

    @Transactional
    public void deleteAccount(Long userId) {
        log.info("Deleting account with id {}", userId);

        deviceTokenService.deleteUserTokens(userId);

        refreshTokenService.revokeAllByUserId(userId);

        tokenService.deleteAllTokensByUserId(userId);

        userService.softDeleteUser(userId);
        log.trace("Deleted account with id {}", userId);
    }

    //endregion

    //region Listeners

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendVerificationEmail(UserRegisteredEvent event) {
        log.trace("Sending verification token to user {}", event.email());
        emailService.sendVerificationEmail(event.email(), event.token());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPasswordResetEmail(PasswordResetRequestedEvent event) {
        log.trace("Sending password reset token to user {}", event.email());
        emailService.sendPasswordResetEmail(event.email(), event.token());
    }

    //endregion

    private void sendVerification(MuddaUser user) {
        tokenService.invalidateUnusedTokens(user.getUserId(), TokenType.EMAIL_VERIFY);
        VerificationToken token = tokenService.generateToken(TokenType.EMAIL_VERIFY, user.getUserId());
        applicationEventPublisher.publishEvent(new UserRegisteredEvent(user.getEmail(), token.getToken()));
        log.debug("Published UserRegisteredEvent for user: {}", user.getUsername());
    }

    private Duration resendCooldown() {
        return Duration.ofMinutes(appProperties.getToken().getResendCooldownMinutes());
    }

}
