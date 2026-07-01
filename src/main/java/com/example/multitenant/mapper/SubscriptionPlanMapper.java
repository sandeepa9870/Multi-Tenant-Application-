package com.example.multitenant.mapper;

import com.example.multitenant.dto.SubscriptionPlanRequest;
import com.example.multitenant.dto.SubscriptionPlanResponse;
import com.example.multitenant.entity.SubscriptionPlan;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionPlanMapper {
    public SubscriptionPlan toEntity(SubscriptionPlanRequest request) {
        return new SubscriptionPlan(request.getPlanName(), request.getDuration(), request.getPrice(), request.getFeatures());
    }

    public SubscriptionPlanResponse toResponse(SubscriptionPlan subscriptionPlan) {
        return new SubscriptionPlanResponse(
            subscriptionPlan.getId(),
            subscriptionPlan.getPlanName(),
            subscriptionPlan.getDuration(),
            subscriptionPlan.getPrice(),
            subscriptionPlan.getFeatures(),
            subscriptionPlan.getStatus(),
            subscriptionPlan.getCreatedAt(),
            subscriptionPlan.getUpdatedAt()
        );
    }

    public void updateEntity(SubscriptionPlanRequest request, SubscriptionPlan subscriptionPlan) {
        subscriptionPlan.setPlanName(request.getPlanName());
        subscriptionPlan.setDuration(request.getDuration());
        subscriptionPlan.setPrice(request.getPrice());
        subscriptionPlan.setFeatures(request.getFeatures());
    }
}

