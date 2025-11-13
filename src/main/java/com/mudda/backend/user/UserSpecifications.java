package com.mudda.backend.user;

import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UserSpecification
 * Author  : Vikas Kumar
 * Created : 13-11-2025
 * ---------------------------------------------------------------
 */
public class UserSpecifications {

    public static Specification<User> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) return null;
            String pattern = "%" + name.trim().toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern);
        };
    }

    public static Specification<User> hasRoleId(Long roleId) {
        return (root, query, criteriaBuilder) ->
                roleId == null ? null : criteriaBuilder.equal(root.get("roleId"), roleId);
    }

    public static Specification<User> createdAfter(Instant date) {
        return (root, query, criteriaBuilder) ->
                date == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<User> createdBefore(Instant date) {
        return (root, query, criteriaBuilder) ->
                date == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), date);
    }
}
