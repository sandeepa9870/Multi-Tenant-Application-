package com.example.multitenant.service;

import com.example.multitenant.dto.TenantRequest;
import com.example.multitenant.dto.TenantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TenantService {
    TenantResponse createTenant(TenantRequest request);
    TenantResponse updateTenant(Long id, TenantRequest request);
    TenantResponse getTenant(Long id);
    Page<TenantResponse> getAllTenants(Pageable pageable);
    Page<TenantResponse> searchTenants(String keyword, Pageable pageable);
    void deleteTenant(Long id);
    void activateTenant(Long id);
    void suspendTenant(Long id);
}

