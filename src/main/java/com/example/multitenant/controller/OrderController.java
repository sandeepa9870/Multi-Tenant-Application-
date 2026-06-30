package com.example.multitenant.controller;

import com.example.multitenant.dto.order.CreateOrderRequest;
import com.example.multitenant.entity.Order;
import com.example.multitenant.entity.User;
import com.example.multitenant.repository.UserRepository;
import com.example.multitenant.service.OrderService;
import com.example.multitenant.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/orders")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.createOrder(user, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(ApiResponse.of("Order created successfully", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getUserOrders(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderService.getUserOrders(user);
        return ResponseEntity.ok(ApiResponse.of("User orders retrieved successfully", orders));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrder(
            @PathVariable java.util.UUID orderId,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.getOrderById(orderId);

        // Ensure user can only see their own orders
        if (!order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized access to order"));
        }

        return ResponseEntity.ok(ApiResponse.of("Order retrieved successfully", order));
    }

    @GetMapping("/order-number/{orderNumber}")
    public ResponseEntity<ApiResponse<Order>> getOrderByNumber(
            @PathVariable String orderNumber,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.getOrderByOrderNumber(orderNumber);

        // Ensure user can only see their own orders
        if (!order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized access to order"));
        }

        return ResponseEntity.ok(ApiResponse.of("Order retrieved successfully", order));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<String>> cancelOrder(
            @PathVariable java.util.UUID orderId,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.getOrderById(orderId);

        // Ensure user can only cancel their own orders
        if (!order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized access to order"));
        }

        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponse.of("Order cancelled successfully", ""));
    }
}

