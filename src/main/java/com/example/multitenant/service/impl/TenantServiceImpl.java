package com.example.multitenant.service.impl;

import com.example.multitenant.dto.TenantRequest;
import com.example.multitenant.dto.TenantResponse;
import com.example.multitenant.entity.Tenant;
import com.example.multitenant.entity.SubscriptionPlan;
import com.example.multitenant.exception.ApiException;
import com.example.multitenant.mapper.TenantMapper;
import com.example.multitenant.repository.TenantRepository;
import com.example.multitenant.repository.SubscriptionPlanRepository;
import com.example.multitenant.service.TenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class TenantServiceImpl implements TenantService {
    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public TenantResponse createTenant(TenantRequest request) {
        log.info("Creating tenant: {}", request.getTenantName());

        if (tenantRepository.findByTenantCode(request.getTenantCode()).isPresent()) {
            throw new ApiException("Tenant code '" + request.getTenantCode() + "' already exists");
        }

        if (request.getEmail() != null && tenantRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Email '" + request.getEmail() + "' already exists");
        }

        Tenant tenant = tenantMapper.toEntity(request);

        if (request.getSubscriptionPlanId() != null) {
            SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getSubscriptionPlanId())
                .orElseThrow(() -> new ApiException("Subscription plan not found with id: " + request.getSubscriptionPlanId()));
            tenant.setSubscriptionPlan(plan);
        }

        tenant = tenantRepository.save(tenant);

        log.info("Tenant created successfully with id: {}", tenant.getId());
        return tenantMapper.toResponse(tenant);
    }

    @Override
    public TenantResponse updateTenant(Long id, TenantRequest request) {
        log.info("Updating tenant with id: {}", id);

        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new ApiException("Tenant not found with id: " + id));

        if (request.getEmail() != null && !tenant.getEmail().equals(request.getEmail()) &&
            tenantRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Email '" + request.getEmail() + "' already exists");
        }

        tenantMapper.updateEntity(request, tenant);

        if (request.getSubscriptionPlanId() != null) {
            SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getSubscriptionPlanId())
                .orElseThrow(() -> new ApiException("Subscription plan not found with id: " + request.getSubscriptionPlanId()));
            tenant.setSubscriptionPlan(plan);
        }

        tenant.setUpdatedAt(LocalDateTime.now());
        tenant = tenantRepository.save(tenant);

        log.info("Tenant updated successfully with id: {}", id);
        return tenantMapper.toResponse(tenant);
    }

    @Override
    public TenantResponse getTenant(Long id) {
        log.info("Fetching tenant with id: {}", id);

        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new ApiException("Tenant not found with id: " + id));

        return tenantMapper.toResponse(tenant);
    }

    @Override
    public Page<TenantResponse> getAllTenants(Pageable pageable) {
        log.info("Fetching all tenants with pagination");

        return tenantRepository.findAll(pageable)
            .map(tenantMapper::toResponse);
    }

    @Override
    public Page<TenantResponse> searchTenants(String keyword, Pageable pageable) {
        log.info("Searching tenants with keyword: {}", keyword);

        return tenantRepository.findByTenantNameContainingIgnoreCaseOrTenantCodeContainingIgnoreCase(
            keyword, keyword, pageable)
            .map(tenantMapper::toResponse);
    }

    @Override
    public void deleteTenant(Long id) {
        log.info("Deleting tenant with id: {}", id);

        if (!tenantRepository.existsById(id)) {
            throw new ApiException("Tenant not found with id: " + id);
        }

        tenantRepository.deleteById(id);
        log.info("Tenant deleted successfully with id: {}", id);
    }

    @Override
    public void activateTenant(Long id) {
        log.info("Activating tenant with id: {}", id);

        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new ApiException("Tenant not found with id: " + id));

        tenant.setStatus(true);
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantRepository.save(tenant);

        log.info("Tenant activated successfully with id: {}", id);
    }

    @Override
    public void suspendTenant(Long id) {
        log.info("Suspending tenant with id: {}", id);

        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new ApiException("Tenant not found with id: " + id));

        tenant.setStatus(false);
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantRepository.save(tenant);

        log.info("Tenant suspended successfully with id: {}", id);
    }
}

