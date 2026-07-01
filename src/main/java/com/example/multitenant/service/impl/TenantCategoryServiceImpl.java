package com.example.multitenant.service.impl;

import com.example.multitenant.dto.TenantCategoryResponse;
import com.example.multitenant.entity.Tenant;
import com.example.multitenant.entity.Category;
import com.example.multitenant.entity.TenantCategory;
import com.example.multitenant.exception.ApiException;
import com.example.multitenant.mapper.TenantCategoryMapper;
import com.example.multitenant.repository.TenantRepository;
import com.example.multitenant.repository.CategoryRepository;
import com.example.multitenant.repository.TenantCategoryRepository;
import com.example.multitenant.service.TenantCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TenantCategoryServiceImpl implements TenantCategoryService {
    @Autowired
    private TenantCategoryRepository tenantCategoryRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TenantCategoryMapper tenantCategoryMapper;

    @Override
    public void assignCategoryToTenant(Long tenantId, Long categoryId, String userEmail) {
        log.info("Assigning category {} to tenant {}", categoryId, tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ApiException("Tenant not found with id: " + tenantId));

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ApiException("Category not found with id: " + categoryId));

        if (tenantCategoryRepository.existsByTenantAndCategory(tenant, category)) {
            throw new ApiException("Category is already assigned to this tenant");
        }

        TenantCategory tenantCategory = new TenantCategory(tenant, category, userEmail);
        tenantCategoryRepository.save(tenantCategory);
        
        log.info("Category assigned successfully to tenant");
    }

    @Override
    public List<TenantCategoryResponse> getTenantCategories(Long tenantId) {
        log.info("Fetching categories for tenant: {}", tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ApiException("Tenant not found with id: " + tenantId));

        return tenantCategoryRepository.findByTenantAndActiveTrue(tenant)
            .stream()
            .map(tenantCategoryMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void removeCategoryFromTenant(Long tenantId, Long categoryId) {
        log.info("Removing category {} from tenant {}", categoryId, tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ApiException("Tenant not found with id: " + tenantId));

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ApiException("Category not found with id: " + categoryId));

        if (!tenantCategoryRepository.existsByTenantAndCategory(tenant, category)) {
            throw new ApiException("Category is not assigned to this tenant");
        }

        tenantCategoryRepository.deleteByTenantAndCategory(tenant, category);
        log.info("Category removed successfully from tenant");
    }
}

