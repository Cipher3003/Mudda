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
import com.mudda.backend.account.VerifyRequest;
import com.mudda.backend.user.CreateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;

    public AuthController(AuthService authService,
                          AccountService accountService) {
        this.authService = authService;
        this.accountService = accountService;
    }

    //    TODO: rate limit
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody CreateUserRequest registrationRequest) {
        accountService.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful.");
    }

    //    TODO: rate limit
    @PostMapping("/verify-email/resend")
    public ResponseEntity<String> retryVerifyEmail(@RequestBody VerifyRequest verifyRequest) {
        accountService.resendEmailVerificationLink(verifyRequest.email());
        return ResponseEntity.ok("If account exists verification link has been sent to email.");
    }

    @GetMapping("/verify-email/confirm")
    public ResponseEntity<String> verifyEmail(@RequestParam String verifyToken) {
        accountService.verifyEmail(verifyToken);
        return ResponseEntity.ok("Email verified successfully.");
    }


    //    TODO: rate limit
    //    Only login when both token expires
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(AuthMapper.toAuthResponse(authService.login(authRequest)));
    }

    @PostMapping("/logout")
    public void logoutUser(@RequestBody RefreshRequest refreshRequest) {
        authService.logout(refreshRequest.refreshToken());
    }

    //    Use to refresh access when expires
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(AuthMapper.toAuthResponse(authService.refresh(refreshRequest.refreshToken())));
    }

//    TODO: rate limit
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        accountService.requestPasswordReset(forgotPasswordRequest.email());
        return ResponseEntity.ok("If account exists, a password reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        accountService.resetPassword(resetPasswordRequest.token(), resetPasswordRequest.password());
        return ResponseEntity.ok("Password Reset successfully, you can now login again.");
    }
}
