package com.example.multitenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Long totalTenants;
    private Long activeTenants;
    private Long suspendedTenants;
    private Long totalProducts;
    private Long totalOrders;
    private Long totalCustomers;
    private Double totalRevenue;
    private Long totalCategories;
    private Long totalTenantAdmins;
}

