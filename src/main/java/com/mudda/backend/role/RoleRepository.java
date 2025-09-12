package com.mudda.backend.role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    public Optional<Role> findByRoleId(Long id);

    public List<Role> findByNameContaining(String text);

}
