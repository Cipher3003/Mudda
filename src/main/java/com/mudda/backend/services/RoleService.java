package com.mudda.backend.services;

import com.mudda.backend.models.Role;
import java.util.Optional;
import java.util.List;

public interface RoleService {
    Role createRole(Role role);
    Optional<Role> findRoleById(Long RoleId);
    List<Role> findAllRoles();
    void deleteRole(Long id);
}