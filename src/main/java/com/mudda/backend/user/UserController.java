package com.mudda.backend.user;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    //    TODO: add proper authentication rules for endpoints
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // #region Queries (Read Operations)

    @GetMapping
    public ResponseEntity<Page<UserSummaryResponse>> getAllUsers(
            @ModelAttribute(name = "filters") UserFilterRequest filterRequest,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "CREATED_AT") UserSortBy sort,
            @RequestParam(name = "sortOrder", defaultValue = "desc") String direction
    ) {
        Pageable pageable = PageRequest.of(
                page, size,
                direction.equalsIgnoreCase("desc")
                        ? Sort.by(sort.getFieldName()).descending()
                        : Sort.by(sort.getFieldName()).ascending());

        return ResponseEntity.ok(userService.findAllUsers(filterRequest, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable(name = "id") long id) {
        return userService.findById(id)
                .map(UserMapper::toDetail)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // #endregion

    // #region Commands (Write Operations)

    @PostMapping
    public ResponseEntity<UserDetailResponse> createUser(@Valid @RequestBody CreateUserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserSummaryResponse> updateUser(@PathVariable(name = "id") long id,
                                                          @RequestBody UpdateUserRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") long id) {
        userService.deleteUser(id);
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    // #endregion
}
