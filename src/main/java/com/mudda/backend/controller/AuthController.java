package com.mudda.backend.controller;

import com.mudda.backend.dto.AuthResponse;
import com.mudda.backend.dto.LoginRequest;
import com.mudda.backend.security.CustomUserDetailsService;
import com.mudda.backend.security.JwtUtil;
import com.mudda.backend.user.User;
import com.mudda.backend.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.mudda.backend.dto.SignupRequest;
import com.mudda.backend.role.Role;
import com.mudda.backend.role.RoleService;
import com.mudda.backend.user.CreateUserRequest;
import com.mudda.backend.user.UserDetailResponse;
import com.mudda.backend.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RoleService roleService;

    public AuthController(AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            UserService userService,
            RoleService roleService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByUserName(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()))
                .orElseThrow();

        return ResponseEntity.ok(new AuthResponse(
                jwt,
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                "USER" // Assuming single role for now, can be fetched from DB if needed
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDetailResponse> signup(@Valid @RequestBody SignupRequest request) {
        // Ensure "USER" role exists, creating it if necessary
        var userRole = roleService.getOrCreateRole("USER");

        CreateUserRequest createUserRequest = new CreateUserRequest(
                request.userName(),
                request.name(),
                request.email(),
                request.dateOfBirth(),
                request.phoneNumber(),
                request.password(),
                userRole.roleId(),
                request.profileImageUrl()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(createUserRequest));
    }
}
