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
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody CreateUserRequest registrationRequest) {
        userService.createUser(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));

        String accessToken = jwtService.generateToken(authRequest.username());
        MuddaUser muddaUser = (MuddaUser) authentication.getPrincipal();

        AuthenticatedUserResponse authenticatedUserResponse = new AuthenticatedUserResponse(
                muddaUser.getUserId(), muddaUser.getUsername(), muddaUser.isEnabled());

        AuthResponse authResponse = new AuthResponse(
                accessToken, "Bearer",
                jwtService.getAccessTokenExpirationTimeMs(), authenticatedUserResponse
        );

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public String refreshToken(@RequestBody CreateUserRequest registrationRequest) {
        return userService.createUser(registrationRequest).toString();
    }

    @PostMapping("/logout")
    public String logoutUser(@RequestBody CreateUserRequest registrationRequest) {
        return userService.createUser(registrationRequest).toString();
    }

    @PostMapping("/verify-email")
    public String verifyUserEmail(@RequestBody CreateUserRequest registrationRequest) {
        return userService.createUser(registrationRequest).toString();
    }
}
