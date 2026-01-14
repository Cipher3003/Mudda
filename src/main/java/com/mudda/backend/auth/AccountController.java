/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AccountController
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

import com.mudda.backend.user.MuddaUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> requestVerification(@RequestBody VerifyRequest verifyRequest) {
        accountService.sendEmailVerificationLink(verifyRequest.email());
        return ResponseEntity.ok("If account exists verification link has been sent to email.");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String verifyToken) {
        accountService.verifyEmail(verifyToken);
        return ResponseEntity.ok("Email verified successfully.");
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(Authentication authentication) {
        Long userId = ((MuddaUser) authentication.getPrincipal()).getUserId();
        accountService.deleteAccount(userId);

        return ResponseEntity.noContent().build();
    }
}
