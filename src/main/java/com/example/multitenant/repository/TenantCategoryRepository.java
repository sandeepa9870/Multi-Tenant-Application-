package com.example.multitenant.repository;

import com.example.multitenant.entity.TenantCategory;
import com.example.multitenant.entity.Tenant;
import com.example.multitenant.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantCategoryRepository extends JpaRepository<TenantCategory, Long> {
    List<TenantCategory> findByTenantAndActiveTrue(Tenant tenant);
    List<TenantCategory> findByTenant(Tenant tenant);
    Optional<TenantCategory> findByTenantAndCategory(Tenant tenant, Category category);
    boolean existsByTenantAndCategory(Tenant tenant, Category category);
    void deleteByTenantAndCategory(Tenant tenant, Category category);
}

