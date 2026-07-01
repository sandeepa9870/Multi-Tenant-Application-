package com.example.multitenant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TenantCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    @Column
    private String assignedBy; // User email who assigned

    @Column(nullable = false)
    private Boolean active = true;

    public TenantCategory(Tenant tenant, Category category, String assignedBy) {
        this.tenant = tenant;
        this.category = category;
        this.assignedBy = assignedBy;
        this.assignedAt = LocalDateTime.now();
        this.active = true;
    }
}

