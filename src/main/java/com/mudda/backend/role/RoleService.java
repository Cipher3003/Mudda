package com.mudda.backend.role;

import java.util.Optional;
import java.util.List;

public interface RoleService {

    List<RoleResponse> findAllRoles(String name);

    Optional<RoleResponse> findById(long id);

    RoleResponse createRole(CreateRoleRequest roleRequest);

    void deleteRole(long id);

}