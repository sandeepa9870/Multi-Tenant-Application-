package com.example.multitenant.controller;

import com.example.multitenant.entity.Role;
import com.example.multitenant.service.RoleService;
import com.example.multitenant.util.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/super-admin/roles")
@Validated
public class SuperAdminRoleController {

    private final RoleService roleService;

    public SuperAdminRoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String description = body.getOrDefault("description", "");
        if (name == null || name.isBlank()) return ResponseEntity.badRequest().body(ApiResponse.error("name is required"));
        Role role = roleService.createRole(name, description);
        return ResponseEntity.ok(ApiResponse.of("Role created", role));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> listRoles() {
        return ResponseEntity.ok(ApiResponse.of("OK", roleService.getAllRoles()));
    }
}

