package com.mudda.backend.postgres.services;

import com.mudda.backend.postgres.models.Role;

import java.util.Optional;
import java.util.List;

public interface RoleService {
    Role createRole(Role role);
    Optional<Role> findRoleById(Long RoleId);
    List<Role> findAllRoles();
    void deleteRole(Long id);
    Optional<Role> findByName(String name);
}