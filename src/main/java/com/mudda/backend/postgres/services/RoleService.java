package com.mudda.backend.postgres.services;

import com.mudda.backend.postgres.models.Role;

import java.util.Optional;
import java.util.List;

public interface RoleService {

    List<Role> findAllRoles();

    Optional<Role> findRoleById(Long id);

    Optional<Role> findRoleByName(String name);

    Role createRole(Role role);

    void deleteRole(Long id);

}