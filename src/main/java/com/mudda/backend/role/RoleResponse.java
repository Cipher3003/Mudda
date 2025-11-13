package com.mudda.backend.role;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoleResponse(
        @JsonProperty("role_id") Long roleId,
        @JsonProperty("role_name") String name
) {

    public static RoleResponse from(Role role) {
        return new RoleResponse(role.getRoleId(), role.getName());
    }

}
