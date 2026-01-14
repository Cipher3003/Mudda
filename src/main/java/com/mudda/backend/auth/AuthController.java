/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AuthController
 * Author  : Vikas Kumar
 * Created : 12-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

import com.mudda.backend.user.CreateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody CreateUserRequest registrationRequest) {
        accountService.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful.");
    }

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

}
