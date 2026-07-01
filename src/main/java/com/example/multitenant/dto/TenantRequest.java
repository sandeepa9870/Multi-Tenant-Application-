package com.example.multitenant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TenantRequest {
    @NotBlank(message = "Tenant name is required")
    @Size(min = 2, max = 100, message = "Tenant name must be between 2 and 100 characters")
    private String tenantName;

    @NotBlank(message = "Tenant code is required")
    @Size(min = 2, max = 50, message = "Tenant code must be between 2 and 50 characters")
    private String tenantCode;

    private String businessName;

    private String ownerName;

    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    private String address;

    private Long subscriptionPlanId;
}

