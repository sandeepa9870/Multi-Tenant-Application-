package com.example.multitenant.mapper;

import com.example.multitenant.dto.TenantRequest;
import com.example.multitenant.dto.TenantResponse;
import com.example.multitenant.dto.SubscriptionPlanResponse;
import com.example.multitenant.entity.Tenant;
import org.springframework.stereotype.Component;

@Component
public class TenantMapper {
    private final SubscriptionPlanMapper subscriptionPlanMapper;

    public TenantMapper(SubscriptionPlanMapper subscriptionPlanMapper) {
        this.subscriptionPlanMapper = subscriptionPlanMapper;
    }

    public Tenant toEntity(TenantRequest request) {
        Tenant tenant = new Tenant(request.getTenantName(), request.getTenantCode());
        tenant.setBusinessName(request.getBusinessName());
        tenant.setOwnerName(request.getOwnerName());
        tenant.setEmail(request.getEmail());
        tenant.setPhone(request.getPhone());
        tenant.setAddress(request.getAddress());
        return tenant;
    }

    public TenantResponse toResponse(Tenant tenant) {
        SubscriptionPlanResponse planResponse = tenant.getSubscriptionPlan() != null
            ? subscriptionPlanMapper.toResponse(tenant.getSubscriptionPlan())
            : null;

        return new TenantResponse(
            tenant.getId(),
            tenant.getTenantName(),
            tenant.getTenantCode(),
            tenant.getBusinessName(),
            tenant.getOwnerName(),
            tenant.getEmail(),
            tenant.getPhone(),
            tenant.getAddress(),
            planResponse,
            tenant.getStatus(),
            tenant.getCreatedAt(),
            tenant.getUpdatedAt()
        );
    }

    public void updateEntity(TenantRequest request, Tenant tenant) {
        tenant.setTenantName(request.getTenantName());
        tenant.setBusinessName(request.getBusinessName());
        tenant.setOwnerName(request.getOwnerName());
        tenant.setEmail(request.getEmail());
        tenant.setPhone(request.getPhone());
        tenant.setAddress(request.getAddress());
    }
}

