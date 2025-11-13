package com.mudda.backend.role;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.exceptions.DuplicateEntityException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // #region Queries (Read Operations)

    @Override
    public Optional<RoleResponse> findById(long id) {
        return roleRepository.findById(id).map(RoleResponse::from);
    }

    @Override
    public List<RoleResponse> findAllRoles(String name) {
        if (name == null || name.isBlank())
            return roleRepository.findByNameContaining(name).stream().map(RoleResponse::from).toList();
        return roleRepository.findAll().stream().map(RoleResponse::from).toList();
    }

    // #endregion

    // #region Commands (Write Operations)

    @Override
    public RoleResponse createRole(CreateRoleRequest roleRequest) {
        Role saved;

        try {
            saved = roleRepository.save(new Role(roleRequest.name()));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntityException("Role with name %s already exists"
                    .formatted(roleRequest.name()));
        }

        return RoleResponse.from(saved);
    }

    @Override
    public void deleteRole(long id) {
        if (!roleRepository.existsById(id))
            throw new EntityNotFoundException("Role with id %d not found".formatted(id));
        roleRepository.deleteById(id);
    }

    // #endregion
}