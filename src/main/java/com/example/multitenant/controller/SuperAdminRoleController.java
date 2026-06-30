package com.example.multitenant.controller;

import com.example.multitenant.entity.Order;
import com.example.multitenant.entity.Role;
import com.example.multitenant.entity.User;
import com.example.multitenant.repository.OrderRepository;
import com.example.multitenant.repository.UserRepository;
import com.example.multitenant.service.OrderService;
import com.example.multitenant.service.RoleService;
import com.example.multitenant.util.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/super-admin")
@Validated
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
public class SuperAdminRoleController {

    private final RoleService roleService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public SuperAdminRoleController(RoleService roleService, UserRepository userRepository,
                                  OrderRepository orderRepository, OrderService orderService) {
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    // ==================== ROLE MANAGEMENT ====================

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String description = body.getOrDefault("description", "");
        if (name == null || name.isBlank()) return ResponseEntity.badRequest().body(ApiResponse.error("name is required"));
        Role role = roleService.createRole(name, description);
        return ResponseEntity.ok(ApiResponse.of("Role created", role));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<Role>>> listRoles() {
        return ResponseEntity.ok(ApiResponse.of("OK", roleService.getAllRoles()));
    }

    // ==================== USER MANAGEMENT ====================

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(ApiResponse.of("All users retrieved successfully", users));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable java.util.UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(ApiResponse.of("User retrieved successfully", user));
    }

    @PutMapping("/users/{id}/enable")
    public ResponseEntity<ApiResponse<User>> enableUser(@PathVariable java.util.UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.of("User enabled successfully", user));
    }

    @PutMapping("/users/{id}/disable")
    public ResponseEntity<ApiResponse<User>> disableUser(@PathVariable java.util.UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.of("User disabled successfully", user));
    }

    @PutMapping("/users/{id}/lock")
    public ResponseEntity<ApiResponse<User>> lockUser(@PathVariable java.util.UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLocked(true);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.of("User locked successfully", user));
    }

    @PutMapping("/users/{id}/unlock")
    public ResponseEntity<ApiResponse<User>> unlockUser(@PathVariable java.util.UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLocked(false);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.of("User unlocked successfully", user));
    }

    @PostMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<User>> assignRoleToUser(@PathVariable java.util.UUID userId, @PathVariable java.util.UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleService.getAllRoles().stream()
                .filter(r -> r.getId().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.of("Role assigned to user successfully", user));
    }

    // ==================== ORDER MANAGEMENT ====================

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(ApiResponse.of("All orders retrieved successfully", orders));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable java.util.UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return ResponseEntity.ok(ApiResponse.of("Order retrieved successfully", order));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(@PathVariable java.util.UUID id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("status is required"));
        }
        Order order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.of("Order status updated successfully", order));
    }

    @GetMapping("/orders/status/{status}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.of("Orders retrieved by status", orders));
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<ApiResponse<List<Order>>> getUserOrders(@PathVariable java.util.UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderRepository.findByUser(user);
        return ResponseEntity.ok(ApiResponse.of("User orders retrieved successfully", orders));
    }
}

