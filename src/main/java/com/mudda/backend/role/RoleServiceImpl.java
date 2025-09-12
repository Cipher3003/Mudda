package com.mudda.backend.role;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<RoleResponse> findRoleById(Long id) {
        return roleRepository.findById(id).map(RoleResponse::from);
    }

    @Override
    public List<RoleResponse> findRoleContainingText(String text) {
        return roleRepository.findByNameContaining(text).stream()
                .map(RoleResponse::from)
                .toList();
    }

    @Override
    public List<RoleResponse> findAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleResponse::from)
                .toList();
    }

    @Override
    public RoleResponse createRole(CreateRoleRequest roleRequest) {
        Role role = new Role(roleRequest.name());
        Role saved = roleRepository.save(role);
        return RoleResponse.from(saved);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id); // not found case is ignored
    }

}