package com.example.multitenant.controller;

import com.example.multitenant.dto.*;
import com.example.multitenant.service.*;
import com.example.multitenant.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/super-admin")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Super Admin", description = "Super Admin Management APIs")
public class SuperAdminController {
    
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private TaxService taxService;

    @Autowired
    private ShippingMethodService shippingMethodService;

    @Autowired
    private SubscriptionPlanService subscriptionPlanService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantCategoryService tenantCategoryService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuditLogService auditLogService;

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    // ==================== DASHBOARD ====================
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard metrics", description = "Get overall platform metrics")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        log.info("Fetching dashboard metrics");
        DashboardResponse dashboard = dashboardService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.of("Dashboard metrics retrieved successfully", dashboard));
    }

    // ==================== CATEGORIES ====================
    @PostMapping("/categories")
    @Operation(summary = "Create category", description = "Create a new category")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request, HttpServletRequest httpRequest) {
        log.info("Creating category: {}", request.getName());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "CREATE", "CATEGORY", getClientIpAddress(httpRequest));
        
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("Category created successfully", response));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Get all categories with pagination")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        log.info("Fetching all categories");
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<CategoryResponse> categories = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(ApiResponse.of("Categories retrieved successfully", categories));
    }

    @GetMapping("/categories/search")
    @Operation(summary = "Search categories", description = "Search categories by name")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> searchCategories(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching categories by name: {}", name);
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryResponse> categories = categoryService.searchCategories(name, pageable);
        return ResponseEntity.ok(ApiResponse.of("Categories found", categories));
    }

    @GetMapping("/categories/{id}")
    @Operation(summary = "Get category by ID", description = "Get a specific category by ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long id) {
        log.info("Fetching category with id: {}", id);
        CategoryResponse category = categoryService.getCategory(id);
        return ResponseEntity.ok(ApiResponse.of("Category retrieved successfully", category));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Update category", description = "Update an existing category")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request, HttpServletRequest httpRequest) {
        log.info("Updating category with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "UPDATE", "CATEGORY", getClientIpAddress(httpRequest));
        
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.of("Category updated successfully", response));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete category", description = "Delete a category")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(@PathVariable Long id, HttpServletRequest httpRequest) {
        log.info("Deleting category with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "DELETE", "CATEGORY", getClientIpAddress(httpRequest));
        
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.of("Category deleted successfully", null));
    }

    @PatchMapping("/categories/{id}/enable")
    @Operation(summary = "Enable category", description = "Enable a disabled category")
    public ResponseEntity<ApiResponse<Object>> enableCategory(@PathVariable Long id, HttpServletRequest httpRequest) {
        log.info("Enabling category with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "STATUS_CHANGE", "CATEGORY", "Enabled", getClientIpAddress(httpRequest));
        
        categoryService.enableCategory(id);
        return ResponseEntity.ok(ApiResponse.of("Category enabled successfully", null));
    }

    @PatchMapping("/categories/{id}/disable")
    @Operation(summary = "Disable category", description = "Disable a category")
    public ResponseEntity<ApiResponse<Object>> disableCategory(@PathVariable Long id, HttpServletRequest httpRequest) {
        log.info("Disabling category with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "STATUS_CHANGE", "CATEGORY", "Disabled", getClientIpAddress(httpRequest));
        
        categoryService.disableCategory(id);
        return ResponseEntity.ok(ApiResponse.of("Category disabled successfully", null));
    }

    // ==================== BRANDS ====================
    @PostMapping("/brands")
    @Operation(summary = "Create brand", description = "Create a new brand")
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody BrandRequest request, HttpServletRequest httpRequest) {
        log.info("Creating brand: {}", request.getName());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "CREATE", "BRAND", getClientIpAddress(httpRequest));
        
        BrandResponse response = brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("Brand created successfully", response));
    }

    @GetMapping("/brands")
    @Operation(summary = "Get all brands", description = "Get all brands with pagination")
    public ResponseEntity<ApiResponse<Page<BrandResponse>>> getAllBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all brands");
        Pageable pageable = PageRequest.of(page, size);
        Page<BrandResponse> brands = brandService.getAllBrands(pageable);
        return ResponseEntity.ok(ApiResponse.of("Brands retrieved successfully", brands));
    }

    @GetMapping("/brands/{id}")
    @Operation(summary = "Get brand by ID", description = "Get a specific brand by ID")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrand(@PathVariable Long id) {
        log.info("Fetching brand with id: {}", id);
        BrandResponse brand = brandService.getBrand(id);
        return ResponseEntity.ok(ApiResponse.of("Brand retrieved successfully", brand));
    }

    @PutMapping("/brands/{id}")
    @Operation(summary = "Update brand", description = "Update an existing brand")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandRequest request, HttpServletRequest httpRequest) {
        log.info("Updating brand with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "UPDATE", "BRAND", getClientIpAddress(httpRequest));
        
        BrandResponse response = brandService.updateBrand(id, request);
        return ResponseEntity.ok(ApiResponse.of("Brand updated successfully", response));
    }

    @DeleteMapping("/brands/{id}")
    @Operation(summary = "Delete brand", description = "Delete a brand")
    public ResponseEntity<ApiResponse<Object>> deleteBrand(@PathVariable Long id, HttpServletRequest httpRequest) {
        log.info("Deleting brand with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "DELETE", "BRAND", getClientIpAddress(httpRequest));
        
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.of("Brand deleted successfully", null));
    }

    // ==================== TAXES ====================
    @PostMapping("/taxes")
    @Operation(summary = "Create tax", description = "Create a new tax")
    public ResponseEntity<ApiResponse<TaxResponse>> createTax(@Valid @RequestBody TaxRequest request, HttpServletRequest httpRequest) {
        log.info("Creating tax: {}", request.getTaxName());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "CREATE", "TAX", getClientIpAddress(httpRequest));
        
        TaxResponse response = taxService.createTax(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("Tax created successfully", response));
    }

    @GetMapping("/taxes")
    @Operation(summary = "Get all taxes", description = "Get all taxes with pagination")
    public ResponseEntity<ApiResponse<Page<TaxResponse>>> getAllTaxes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all taxes");
        Pageable pageable = PageRequest.of(page, size);
        Page<TaxResponse> taxes = taxService.getAllTaxes(pageable);
        return ResponseEntity.ok(ApiResponse.of("Taxes retrieved successfully", taxes));
    }

    @GetMapping("/taxes/{id}")
    @Operation(summary = "Get tax by ID", description = "Get a specific tax by ID")
    public ResponseEntity<ApiResponse<TaxResponse>> getTax(@PathVariable Long id) {
        log.info("Fetching tax with id: {}", id);
        TaxResponse tax = taxService.getTax(id);
        return ResponseEntity.ok(ApiResponse.of("Tax retrieved successfully", tax));
    }

    @PutMapping("/taxes/{id}")
    @Operation(summary = "Update tax", description = "Update an existing tax")
    public ResponseEntity<ApiResponse<TaxResponse>> updateTax(@PathVariable Long id, @Valid @RequestBody TaxRequest request, HttpServletRequest httpRequest) {
        log.info("Updating tax with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "UPDATE", "TAX", getClientIpAddress(httpRequest));
        
        TaxResponse response = taxService.updateTax(id, request);
        return ResponseEntity.ok(ApiResponse.of("Tax updated successfully", response));
    }

    @DeleteMapping("/taxes/{id}")
    @Operation(summary = "Delete tax", description = "Delete a tax")
    public ResponseEntity<ApiResponse<Object>> deleteTax(@PathVariable Long id, HttpServletRequest httpRequest) {
        log.info("Deleting tax with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "DELETE", "TAX", getClientIpAddress(httpRequest));
        
        taxService.deleteTax(id);
        return ResponseEntity.ok(ApiResponse.of("Tax deleted successfully", null));
    }

    // ==================== SHIPPING METHODS ====================
    @PostMapping("/shipping-methods")
    @Operation(summary = "Create shipping method", description = "Create a new shipping method")
    public ResponseEntity<ApiResponse<ShippingMethodResponse>> createShippingMethod(@Valid @RequestBody ShippingMethodRequest request, HttpServletRequest httpRequest) {
        log.info("Creating shipping method: {}", request.getShippingName());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "CREATE", "SHIPPING", getClientIpAddress(httpRequest));
        
        ShippingMethodResponse response = shippingMethodService.createShippingMethod(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("Shipping method created successfully", response));
    }

    @GetMapping("/shipping-methods")
    @Operation(summary = "Get all shipping methods", description = "Get all shipping methods with pagination")
    public ResponseEntity<ApiResponse<Page<ShippingMethodResponse>>> getAllShippingMethods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all shipping methods");
        Pageable pageable = PageRequest.of(page, size);
        Page<ShippingMethodResponse> methods = shippingMethodService.getAllShippingMethods(pageable);
        return ResponseEntity.ok(ApiResponse.of("Shipping methods retrieved successfully", methods));
    }

    @GetMapping("/shipping-methods/{id}")
    @Operation(summary = "Get shipping method by ID", description = "Get a specific shipping method by ID")
    public ResponseEntity<ApiResponse<ShippingMethodResponse>> getShippingMethod(@PathVariable Long id) {
        log.info("Fetching shipping method with id: {}", id);
        ShippingMethodResponse method = shippingMethodService.getShippingMethod(id);
        return ResponseEntity.ok(ApiResponse.of("Shipping method retrieved successfully", method));
    }

    @PutMapping("/shipping-methods/{id}")
    @Operation(summary = "Update shipping method", description = "Update an existing shipping method")
    public ResponseEntity<ApiResponse<ShippingMethodResponse>> updateShippingMethod(@PathVariable Long id, @Valid @RequestBody ShippingMethodRequest request, HttpServletRequest httpRequest) {
        log.info("Updating shipping method with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "UPDATE", "SHIPPING", getClientIpAddress(httpRequest));
        
        ShippingMethodResponse response = shippingMethodService.updateShippingMethod(id, request);
        return ResponseEntity.ok(ApiResponse.of("Shipping method updated successfully", response));
    }

    @DeleteMapping("/shipping-methods/{id}")
    @Operation(summary = "Delete shipping method", description = "Delete a shipping method")
    public ResponseEntity<ApiResponse<Object>> deleteShippingMethod(@PathVariable Long id, HttpServletRequest httpRequest) {
        log.info("Deleting shipping method with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "DELETE", "SHIPPING", getClientIpAddress(httpRequest));
        
        shippingMethodService.deleteShippingMethod(id);
        return ResponseEntity.ok(ApiResponse.of("Shipping method deleted successfully", null));
    }

    // ==================== SUBSCRIPTION PLANS ====================
    @PostMapping("/subscriptions")
    @Operation(summary = "Create subscription plan", description = "Create a new subscription plan")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> createSubscriptionPlan(@Valid @RequestBody SubscriptionPlanRequest request, HttpServletRequest httpRequest) {
        log.info("Creating subscription plan: {}", request.getPlanName());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "CREATE", "SUBSCRIPTION", getClientIpAddress(httpRequest));
        
        SubscriptionPlanResponse response = subscriptionPlanService.createSubscriptionPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("Subscription plan created successfully", response));
    }

    @GetMapping("/subscriptions")
    @Operation(summary = "Get all subscription plans", description = "Get all subscription plans with pagination")
    public ResponseEntity<ApiResponse<Page<SubscriptionPlanResponse>>> getAllSubscriptionPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all subscription plans");
        Pageable pageable = PageRequest.of(page, size);
        Page<SubscriptionPlanResponse> plans = subscriptionPlanService.getAllSubscriptionPlans(pageable);
        return ResponseEntity.ok(ApiResponse.of("Subscription plans retrieved successfully", plans));
    }

    @GetMapping("/subscriptions/{id}")
    @Operation(summary = "Get subscription plan by ID", description = "Get a specific subscription plan by ID")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> getSubscriptionPlan(@PathVariable Long id) {
        log.info("Fetching subscription plan with id: {}", id);
        SubscriptionPlanResponse plan = subscriptionPlanService.getSubscriptionPlan(id);
        return ResponseEntity.ok(ApiResponse.of("Subscription plan retrieved successfully", plan));
    }

    @PutMapping("/subscriptions/{id}")
    @Operation(summary = "Update subscription plan", description = "Update an existing subscription plan")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> updateSubscriptionPlan(@PathVariable Long id, @Valid @RequestBody SubscriptionPlanRequest request, HttpServletRequest httpRequest) {
        log.info("Updating subscription plan with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "UPDATE", "SUBSCRIPTION", getClientIpAddress(httpRequest));
        
        SubscriptionPlanResponse response = subscriptionPlanService.updateSubscriptionPlan(id, request);
        return ResponseEntity.ok(ApiResponse.of("Subscription plan updated successfully", response));
    }

    @DeleteMapping("/subscriptions/{id}")
    @Operation(summary = "Delete subscription plan", description = "Delete a subscription plan")
    public ResponseEntity<ApiResponse<Object>> deleteSubscriptionPlan(@PathVariable Long id, HttpServletRequest httpRequest) {
        log.info("Deleting subscription plan with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "DELETE", "SUBSCRIPTION", getClientIpAddress(httpRequest));
        
        subscriptionPlanService.deleteSubscriptionPlan(id);
        return ResponseEntity.ok(ApiResponse.of("Subscription plan deleted successfully", null));
    }

    // ==================== TENANTS ====================
    @PostMapping("/tenants")
    @Operation(summary = "Create tenant", description = "Create a new tenant")
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(@Valid @RequestBody TenantRequest request, HttpServletRequest httpRequest) {
        log.info("Creating tenant: {}", request.getTenantName());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "CREATE", "TENANT", getClientIpAddress(httpRequest));
        
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("Tenant created successfully", response));
    }

    @GetMapping("/tenants")
    @Operation(summary = "Get all tenants", description = "Get all tenants with pagination")
    public ResponseEntity<ApiResponse<Page<TenantResponse>>> getAllTenants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "tenantName") String sortBy) {
        log.info("Fetching all tenants");
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<TenantResponse> tenants = tenantService.getAllTenants(pageable);
        return ResponseEntity.ok(ApiResponse.of("Tenants retrieved successfully", tenants));
    }

    @GetMapping("/tenants/search")
    @Operation(summary = "Search tenants", description = "Search tenants by name or code")
    public ResponseEntity<ApiResponse<Page<TenantResponse>>> searchTenants(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Searching tenants with keyword: {}", keyword);
        Pageable pageable = PageRequest.of(page, size);
        Page<TenantResponse> tenants = tenantService.searchTenants(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.of("Tenants found", tenants));
    }

    @GetMapping("/tenants/{id}")
    @Operation(summary = "Get tenant by ID", description = "Get a specific tenant by ID")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenant(@PathVariable Long id) {
        log.info("Fetching tenant with id: {}", id);
        TenantResponse tenant = tenantService.getTenant(id);
        return ResponseEntity.ok(ApiResponse.of("Tenant retrieved successfully", tenant));
    }

    @PutMapping("/tenants/{id}")
    @Operation(summary = "Update tenant", description = "Update an existing tenant")
    public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(@PathVariable Long id, @Valid @RequestBody TenantRequest request, HttpServletRequest httpRequest) {
        log.info("Updating tenant with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "UPDATE", "TENANT", getClientIpAddress(httpRequest));
        
        TenantResponse response = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(ApiResponse.of("Tenant updated successfully", response));
    }

    @DeleteMapping("/tenants/{id}")
    @Operation(summary = "Delete tenant", description = "Delete a tenant")
    public ResponseEntity<ApiResponse<Object>> deleteTenant(@PathVariable Long id, HttpServletRequest httpRequest) {
        log.info("Deleting tenant with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "DELETE", "TENANT", getClientIpAddress(httpRequest));
        
        tenantService.deleteTenant(id);
        return ResponseEntity.ok(ApiResponse.of("Tenant deleted successfully", null));
    }

    @PatchMapping("/tenants/{id}/activate")
    @Operation(summary = "Activate tenant", description = "Activate a suspended tenant")
    public ResponseEntity<ApiResponse<Object>> activateTenant(@PathVariable Long id, HttpServletRequest httpRequest) {
        log.info("Activating tenant with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "STATUS_CHANGE", "TENANT", "Activated", getClientIpAddress(httpRequest));
        
        tenantService.activateTenant(id);
        return ResponseEntity.ok(ApiResponse.of("Tenant activated successfully", null));
    }

    @PatchMapping("/tenants/{id}/suspend")
    @Operation(summary = "Suspend tenant", description = "Suspend an active tenant")
    public ResponseEntity<ApiResponse<Object>> suspendTenant(@PathVariable Long id, HttpServletRequest httpRequest) {
        log.info("Suspending tenant with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "STATUS_CHANGE", "TENANT", "Suspended", getClientIpAddress(httpRequest));
        
        tenantService.suspendTenant(id);
        return ResponseEntity.ok(ApiResponse.of("Tenant suspended successfully", null));
    }

    // ==================== TENANT CATEGORIES ====================
    @PostMapping("/tenants/{tenantId}/categories")
    @Operation(summary = "Assign category to tenant", description = "Assign a category to a tenant")
    public ResponseEntity<ApiResponse<Object>> assignCategoryToTenant(
            @PathVariable Long tenantId,
            @Valid @RequestBody TenantCategoryRequest request,
            HttpServletRequest httpRequest) {
        log.info("Assigning category {} to tenant {}", request.getCategoryId(), tenantId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "CREATE", "TENANT_CATEGORY", getClientIpAddress(httpRequest));
        
        tenantCategoryService.assignCategoryToTenant(tenantId, request.getCategoryId(), auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("Category assigned to tenant successfully", null));
    }

    @GetMapping("/tenants/{tenantId}/categories")
    @Operation(summary = "Get tenant categories", description = "Get all categories assigned to a tenant")
    public ResponseEntity<ApiResponse<java.util.List<TenantCategoryResponse>>> getTenantCategories(@PathVariable Long tenantId) {
        log.info("Fetching categories for tenant: {}", tenantId);
        java.util.List<TenantCategoryResponse> categories = tenantCategoryService.getTenantCategories(tenantId);
        return ResponseEntity.ok(ApiResponse.of("Tenant categories retrieved successfully", categories));
    }

    @DeleteMapping("/tenants/{tenantId}/categories/{categoryId}")
    @Operation(summary = "Remove category from tenant", description = "Remove a category from a tenant")
    public ResponseEntity<ApiResponse<Object>> removeCategoryFromTenant(
            @PathVariable Long tenantId,
            @PathVariable Long categoryId,
            HttpServletRequest httpRequest) {
        log.info("Removing category {} from tenant {}", categoryId, tenantId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auditLogService.logAction(auth.getName(), "DELETE", "TENANT_CATEGORY", getClientIpAddress(httpRequest));
        
        tenantCategoryService.removeCategoryFromTenant(tenantId, categoryId);
        return ResponseEntity.ok(ApiResponse.of("Category removed from tenant successfully", null));
    }

    // ==================== AUDIT LOGS ====================
    @GetMapping("/audit-logs")
    @Operation(summary = "Get audit logs", description = "Get all audit logs with pagination")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching audit logs");
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLogResponse> logs = auditLogService.getAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponse.of("Audit logs retrieved successfully", logs));
    }

    @GetMapping("/audit-logs/user/{userEmail}")
    @Operation(summary = "Get audit logs by user", description = "Get audit logs for a specific user")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogsByUser(
            @PathVariable String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching audit logs for user: {}", userEmail);
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLogResponse> logs = auditLogService.getAuditLogsByUser(userEmail, pageable);
        return ResponseEntity.ok(ApiResponse.of("Audit logs retrieved successfully", logs));
    }

    @GetMapping("/audit-logs/module/{module}")
    @Operation(summary = "Get audit logs by module", description = "Get audit logs for a specific module")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogsByModule(
            @PathVariable String module,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching audit logs for module: {}", module);
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLogResponse> logs = auditLogService.getAuditLogsByModule(module, pageable);
        return ResponseEntity.ok(ApiResponse.of("Audit logs retrieved successfully", logs));
    }
}

