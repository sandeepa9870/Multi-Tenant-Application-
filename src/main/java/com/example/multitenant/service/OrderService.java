package com.example.multitenant.service;

import com.example.multitenant.entity.Order;
import com.example.multitenant.entity.User;
import java.util.UUID;
import java.util.List;

public interface OrderService {
    Order createOrder(User user, UUID productId, Integer quantity);
    Order getOrderById(UUID id);
    Order getOrderByOrderNumber(String orderNumber);
    List<Order> getUserOrders(User user);
    List<Order> getAllOrders();
    Order updateOrderStatus(UUID orderId, String status);
    void cancelOrder(UUID orderId);
    List<Order> getOrdersByStatus(String status);
}

