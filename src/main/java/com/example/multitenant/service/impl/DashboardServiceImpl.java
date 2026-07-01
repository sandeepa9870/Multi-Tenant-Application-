package com.example.multitenant.service.impl;

import com.example.multitenant.dto.DashboardResponse;
import com.example.multitenant.repository.TenantRepository;
import com.example.multitenant.repository.CategoryRepository;
import com.example.multitenant.repository.UserRepository;
import com.example.multitenant.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public DashboardResponse getDashboardMetrics() {
        log.info("Fetching dashboard metrics");

        DashboardResponse dashboard = new DashboardResponse();

        // TODO: Implement full metrics based on actual business requirements
        // For now, basic counts are provided
        dashboard.setTotalTenants(tenantRepository.count());
        dashboard.setActiveTenants(tenantRepository.countByStatusTrue());
        dashboard.setSuspendedTenants(tenantRepository.countByStatusFalse());
        dashboard.setTotalCategories(categoryRepository.count());

        // These would be implemented when product, order, and customer entities are created
        dashboard.setTotalProducts(0L);
        dashboard.setTotalOrders(0L);
        dashboard.setTotalCustomers(0L);
        dashboard.setTotalRevenue(0.0);
        dashboard.setTotalTenantAdmins(getTenantAdminCount());

        log.info("Dashboard metrics fetched successfully");
        return dashboard;
    }

    private Long getTenantAdminCount() {
        // Count users with TENANT_ADMIN role
        // This will be implemented when role-based counting is available
        return 0L;
    }
}

