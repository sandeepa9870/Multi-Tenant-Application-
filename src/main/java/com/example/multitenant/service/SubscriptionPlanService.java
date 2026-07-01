package com.example.multitenant.service;

import com.example.multitenant.dto.SubscriptionPlanRequest;
import com.example.multitenant.dto.SubscriptionPlanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubscriptionPlanService {
    SubscriptionPlanResponse createSubscriptionPlan(SubscriptionPlanRequest request);
    SubscriptionPlanResponse updateSubscriptionPlan(Long id, SubscriptionPlanRequest request);
    SubscriptionPlanResponse getSubscriptionPlan(Long id);
    Page<SubscriptionPlanResponse> getAllSubscriptionPlans(Pageable pageable);
    void deleteSubscriptionPlan(Long id);
}

