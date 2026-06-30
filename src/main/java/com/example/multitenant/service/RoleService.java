package com.example.multitenant.service;

import com.example.multitenant.entity.Role;

import java.util.List;

public interface RoleService {
    Role createRole(String name, String description);
    List<Role> getAllRoles();
}

