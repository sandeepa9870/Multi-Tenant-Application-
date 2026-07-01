package com.example.multitenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean enabled;
    private Boolean emailVerified;
    private Boolean accountLocked;
    private String roleName;
    private String tenantName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}

