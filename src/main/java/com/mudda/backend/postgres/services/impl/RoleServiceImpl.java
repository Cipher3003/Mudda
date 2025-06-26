package com.mudda.backend.postgres.services.impl;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.postgres.models.Role;
import com.mudda.backend.postgres.repositories.RoleRepository;
import com.mudda.backend.postgres.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;


    public Role createRole(Role role) { 
        return roleRepository.save(role);
    }

    @Override
    public Optional<Role> findRoleById(Long roleId) {
        return roleRepository.findById(roleId);
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
}