package com.example.multitenant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenantName;

    @Column(nullable = false, unique = true)
    private String tenantCode;

    @Column(nullable = false)
    private Boolean status = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Tenant(String tenantName, String tenantCode) {
        this.tenantName = tenantName;
        this.tenantCode = tenantCode;
        this.status = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

