package com.example.multitenant.service.impl;

import com.example.multitenant.dto.SubscriptionPlanRequest;
import com.example.multitenant.dto.SubscriptionPlanResponse;
import com.example.multitenant.entity.SubscriptionPlan;
import com.example.multitenant.exception.ApiException;
import com.example.multitenant.mapper.SubscriptionPlanMapper;
import com.example.multitenant.repository.SubscriptionPlanRepository;
import com.example.multitenant.service.SubscriptionPlanService;
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
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {
    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private SubscriptionPlanMapper subscriptionPlanMapper;

    @Override
    public SubscriptionPlanResponse createSubscriptionPlan(SubscriptionPlanRequest request) {
        log.info("Creating subscription plan: {}", request.getPlanName());

        if (subscriptionPlanRepository.existsByPlanName(request.getPlanName())) {
            throw new ApiException("Subscription plan with name '" + request.getPlanName() + "' already exists");
        }

        SubscriptionPlan plan = subscriptionPlanMapper.toEntity(request);
        plan = subscriptionPlanRepository.save(plan);

        log.info("Subscription plan created successfully with id: {}", plan.getId());
        return subscriptionPlanMapper.toResponse(plan);
    }

    @Override
    public SubscriptionPlanResponse updateSubscriptionPlan(Long id, SubscriptionPlanRequest request) {
        log.info("Updating subscription plan with id: {}", id);

        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
            .orElseThrow(() -> new ApiException("Subscription plan not found with id: " + id));

        if (!plan.getPlanName().equals(request.getPlanName()) &&
            subscriptionPlanRepository.existsByPlanName(request.getPlanName())) {
            throw new ApiException("Subscription plan with name '" + request.getPlanName() + "' already exists");
        }

        subscriptionPlanMapper.updateEntity(request, plan);
        plan.setUpdatedAt(LocalDateTime.now());
        plan = subscriptionPlanRepository.save(plan);

        log.info("Subscription plan updated successfully with id: {}", id);
        return subscriptionPlanMapper.toResponse(plan);
    }

    @Override
    public SubscriptionPlanResponse getSubscriptionPlan(Long id) {
        log.info("Fetching subscription plan with id: {}", id);

        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
            .orElseThrow(() -> new ApiException("Subscription plan not found with id: " + id));

        return subscriptionPlanMapper.toResponse(plan);
    }

    @Override
    public Page<SubscriptionPlanResponse> getAllSubscriptionPlans(Pageable pageable) {
        log.info("Fetching all subscription plans with pagination");

        return subscriptionPlanRepository.findAll(pageable)
            .map(subscriptionPlanMapper::toResponse);
    }

    @Override
    public void deleteSubscriptionPlan(Long id) {
        log.info("Deleting subscription plan with id: {}", id);

        if (!subscriptionPlanRepository.existsById(id)) {
            throw new ApiException("Subscription plan not found with id: " + id);
        }

        subscriptionPlanRepository.deleteById(id);
        log.info("Subscription plan deleted successfully with id: {}", id);
    }
}

