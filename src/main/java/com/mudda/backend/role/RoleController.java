package com.mudda.backend.role;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // #region Queries (Read Operations)

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAll(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(roleService.findAllRoles(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getById(@PathVariable(name = "id") long id) {
        return roleService.findById(id)
                .map(ResponseEntity::ok) // 200 ok
                .orElse(ResponseEntity.notFound().build()); // 404 not found
    }

    // #endregion

    // #region Commands (Write Operations)

    @PostMapping
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody CreateRoleRequest roleRequest) {
        return ResponseEntity.ok(roleService.createRole(roleRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    // #endregion
}
