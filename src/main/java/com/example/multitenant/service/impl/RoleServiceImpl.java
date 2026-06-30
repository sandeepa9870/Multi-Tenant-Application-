package com.example.multitenant.service.impl;

import com.example.multitenant.entity.Role;
import com.example.multitenant.exception.ApiException;
import com.example.multitenant.repository.RoleRepository;
import com.example.multitenant.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role createRole(String name, String description) {
        if (roleRepository.existsByName(name)) throw new ApiException("Role already exists");
        Role role = new Role(name, description);
        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}

