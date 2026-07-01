package com.example.multitenant.repository;

import com.example.multitenant.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByTenantCode(String tenantCode);
    Optional<Tenant> findByTenantName(String tenantName);
    Optional<Tenant> findByEmail(String email);
    Page<Tenant> findByTenantNameContainingIgnoreCaseOrTenantCodeContainingIgnoreCase(String tenantName, String tenantCode, Pageable pageable);
    long countByStatusTrue();
    long countByStatusFalse();
}

