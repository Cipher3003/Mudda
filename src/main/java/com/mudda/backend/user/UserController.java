package com.mudda.backend.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserSummaryResponse>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users.stream()
                .map(UserSummaryResponse::from)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable(name = "id") Long id) {
        return userService.findUserById(id)
                .map(UserDetailResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get by Email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDetailResponse> getUserByEmail(@PathVariable(name = "email") String email) {
        return userService.findByEmail(email)
                .map(UserDetailResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/phone/{number}")
    public ResponseEntity<UserDetailResponse> getUserByPhoneNumber(@PathVariable(name = "number") String number) {
        return userService.findByPhoneNumber(number)
                .map(UserDetailResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDetailResponse> createUser(@Valid @RequestBody CreateUserRequest userRequest) {
        User saved = userService.createUser(userRequest);
        return ResponseEntity.ok(UserDetailResponse.from(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserSummaryResponse> updateUser(@PathVariable(name = "id") Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(UserSummaryResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
