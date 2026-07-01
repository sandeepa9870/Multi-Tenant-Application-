package com.example.multitenant.mapper;

import com.example.multitenant.dto.TenantCategoryResponse;
import com.example.multitenant.entity.TenantCategory;
import org.springframework.stereotype.Component;

@Component
public class TenantCategoryMapper {
    private final TenantMapper tenantMapper;
    private final CategoryMapper categoryMapper;

    public TenantCategoryMapper(TenantMapper tenantMapper, CategoryMapper categoryMapper) {
        this.tenantMapper = tenantMapper;
        this.categoryMapper = categoryMapper;
    }

    public TenantCategoryResponse toResponse(TenantCategory tenantCategory) {
        return new TenantCategoryResponse(
            tenantCategory.getId(),
            tenantMapper.toResponse(tenantCategory.getTenant()),
            categoryMapper.toResponse(tenantCategory.getCategory()),
            tenantCategory.getAssignedAt(),
            tenantCategory.getAssignedBy(),
            tenantCategory.getActive()
        );
    }
}

