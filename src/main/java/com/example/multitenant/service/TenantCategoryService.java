package com.example.multitenant.service;

import com.example.multitenant.dto.TenantCategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TenantCategoryService {
    void assignCategoryToTenant(Long tenantId, Long categoryId, String userEmail);
    List<TenantCategoryResponse> getTenantCategories(Long tenantId);
    void removeCategoryFromTenant(Long tenantId, Long categoryId);
}

