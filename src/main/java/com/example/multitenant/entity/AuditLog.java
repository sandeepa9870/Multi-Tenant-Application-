package com.example.multitenant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String action; // LOGIN, LOGOUT, CREATE, UPDATE, DELETE, STATUS_CHANGE

    @Column(nullable = false)
    private String module; // TENANT, USER, CATEGORY, BRAND, TAX, SHIPPING, SUBSCRIPTION

    @Column(columnDefinition = "TEXT")
    private String details; // JSON with additional details

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column
    private String ipAddress;

    public AuditLog(String userEmail, String action, String module, String ipAddress) {
        this.userEmail = userEmail;
        this.action = action;
        this.module = module;
        this.ipAddress = ipAddress;
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog(String userEmail, String action, String module, String details, String ipAddress) {
        this(userEmail, action, module, ipAddress);
        this.details = details;
    }
}

