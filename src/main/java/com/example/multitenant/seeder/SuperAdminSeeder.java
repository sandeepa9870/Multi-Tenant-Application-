package com.example.multitenant.seeder;

import com.example.multitenant.entity.Role;
import com.example.multitenant.entity.User;
import com.example.multitenant.repository.RoleRepository;
import com.example.multitenant.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class SuperAdminSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!roleRepository.existsByName("ROLE_SUPER_ADMIN")) {
            roleRepository.save(new Role("ROLE_SUPER_ADMIN", "Super administrator with full access"));
        }
        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            roleRepository.save(new Role("ROLE_ADMIN", "Organization admin"));
        }
        if (!roleRepository.existsByName("ROLE_USER")) {
            roleRepository.save(new Role("ROLE_USER", "End user"));
        }

        String superAdminEmail = "superadmin@example.com";
        if (!userRepository.existsByEmail(superAdminEmail)) {
            User superAdmin = new User();
            superAdmin.setFirstName("Super");
            superAdmin.setLastName("Admin");
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setPassword(passwordEncoder.encode("ChangeMe123!"));
            superAdmin.setEnabled(true);
            Role superRole = roleRepository.findByName("ROLE_SUPER_ADMIN").orElseThrow();
            superAdmin.setRoles(new HashSet<>());
            superAdmin.getRoles().add(superRole);
            userRepository.save(superAdmin);
        }
    }
}

