package com.library.service;

import com.library.entity.Role;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for role management operations.
 * Handles role CRUD operations.
 */
@Service
@Transactional
public class RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Get all roles.
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Get role by ID.
     */
    public Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
    }

    /**
     * Get role by name.
     */
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
    }

    /**
     * Create a new role.
     */
    public Role createRole(String name, String description) {
        // Check if role already exists
        if (roleRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Role already exists: " + name);
        }

        Role role = new Role(name, description);

        Role savedRole = roleRepository.save(role);
        logger.info("Role created successfully: {}", name);
        return savedRole;
    }

    /**
     * Update role description.
     */
    public Role updateRole(Long roleId, String description) {
        Role role = getRoleById(roleId);
        role.setDescription(description);

        Role updatedRole = roleRepository.save(role);
        logger.info("Role updated successfully: {}", role.getName());
        return updatedRole;
    }

    /**
     * Delete role.
     */
    public void deleteRole(Long roleId) {
        Role role = getRoleById(roleId);
        roleRepository.delete(role);
        logger.info("Role deleted: {}", role.getName());
    }

}
