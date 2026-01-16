/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AuthController
 * Author  : Vikas Kumar
 * Created : 12-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

import com.mudda.backend.account.AccountService;
import com.mudda.backend.user.CreateUserRequest;
import com.mudda.backend.utils.MessageCodes;
import com.mudda.backend.utils.MessageUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;
    private final MessageUtil messageUtil;

    public AuthController(AuthService authService,
                          AccountService accountService,
                          MessageUtil messageUtil) {
        this.authService = authService;
        this.accountService = accountService;
        this.messageUtil = messageUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody CreateUserRequest registrationRequest) {
        accountService.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse(messageUtil.getMessage(MessageCodes.REGISTRATION_SUCCESS)));
    }

    @PostMapping("/verify-email/resend")
    public ResponseEntity<MessageResponse> retryVerifyEmail(@Valid @RequestBody VerifyRequest verifyRequest) {
        accountService.resendEmailVerificationLink(verifyRequest.email());
        return ResponseEntity.ok(new MessageResponse(messageUtil.getMessage(MessageCodes.VERIFICATION_EMAIL_SENT)));
    }

    @GetMapping("/verify-email/confirm")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam @NotBlank String verifyToken) {
        accountService.verifyEmail(verifyToken);
        return ResponseEntity.ok(new MessageResponse(messageUtil.getMessage(MessageCodes.EMAIL_VERIFIED)));
    }

    //    Only login when both token expires
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(AuthMapper.toAuthResponse(authService.login(authRequest)));
    }

    @PostMapping("/logout")
    public void logoutUser(@Valid @RequestBody RefreshRequest refreshRequest) {
        authService.logout(refreshRequest.refreshToken());
    }

    //    Use to refresh access when expires
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(AuthMapper.toAuthResponse(authService.refresh(refreshRequest.refreshToken())));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest
    ) {
        accountService.requestPasswordReset(forgotPasswordRequest.email());
        return ResponseEntity.ok(new MessageResponse(messageUtil.getMessage(MessageCodes.PASSWORD_RESET_LINK_SENT)));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest
    ) {
        accountService.resetPassword(resetPasswordRequest.token(), resetPasswordRequest.password());
        return ResponseEntity.ok(new MessageResponse(messageUtil.getMessage(MessageCodes.PASSWORD_RESET_SUCCESS)));
    }
}
