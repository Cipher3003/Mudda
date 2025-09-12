package com.mudda.backend.role;

import java.util.Optional;
import java.util.List;

public interface RoleService {

    List<RoleResponse> findAllRoles();

    Optional<RoleResponse> findRoleById(Long id);

    List<RoleResponse> findRoleContainingText(String text);

    RoleResponse createRole(CreateRoleRequest roleRequest);

    void deleteRole(Long id);

}