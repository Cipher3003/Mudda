package com.mudda.backend.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByNameContaining(String text);

    java.util.Optional<Role> findByName(String name);

}
