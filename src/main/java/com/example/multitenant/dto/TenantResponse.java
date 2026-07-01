package com.example.multitenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponse {
    private Long id;
    private String tenantName;
    private String tenantCode;
    private String businessName;
    private String ownerName;
    private String email;
    private String phone;
    private String address;
    private SubscriptionPlanResponse subscriptionPlan;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

