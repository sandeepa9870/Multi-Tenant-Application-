package com.example.multitenant.seeder;

import com.example.multitenant.entity.Role;
import com.example.multitenant.entity.User;
import com.example.multitenant.repository.RoleRepository;
import com.example.multitenant.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataSeeder implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data seeding...");

        // Seed roles
        seedRoles();

        // Seed super admin user
        seedSuperAdmin();

        log.info("Data seeding completed successfully");
    }

    private void seedRoles() {
        String[] roleNames = {"SUPER_ADMIN", "TENANT_ADMIN", "EMPLOYEE", "CUSTOMER"};

        for (String roleName : roleNames) {
            if (roleRepository.findByRoleName(roleName).isEmpty()) {
                Role role = new Role(roleName);
                roleRepository.save(role);
                log.info("Role created: {}", roleName);
            } else {
                log.info("Role already exists: {}", roleName);
            }
        }
    }

    private void seedSuperAdmin() {
        String superAdminEmail = "superadmin@multitenant.com";

        if (userRepository.findByEmail(superAdminEmail).isEmpty()) {
            Role superAdminRole = roleRepository.findByRoleName("SUPER_ADMIN")
                    .orElseThrow(() -> new RuntimeException("SUPER_ADMIN role not found"));

            User superAdmin = new User();
            superAdmin.setFirstName("Super");
            superAdmin.setLastName("Admin");
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setPhone("+1-800-000-0000");
            superAdmin.setPassword(passwordEncoder.encode("Admin@123"));
            superAdmin.setEnabled(true);
            superAdmin.setEmailVerified(true);
            superAdmin.setAccountLocked(false);
            superAdmin.setRole(superAdminRole);
            superAdmin.setTenant(null); // Super admin doesn't belong to any tenant

            userRepository.save(superAdmin);
            log.info("Super admin user created: {}", superAdminEmail);
        } else {
            log.info("Super admin user already exists: {}", superAdminEmail);
        }
    }
}

