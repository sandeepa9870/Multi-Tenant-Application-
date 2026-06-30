package com.example.multitenant.service.impl;

import com.example.multitenant.entity.Order;
import com.example.multitenant.entity.Product;
import com.example.multitenant.entity.User;
import com.example.multitenant.exception.ApiException;
import com.example.multitenant.repository.OrderRepository;
import com.example.multitenant.repository.ProductRepository;
import com.example.multitenant.service.OrderService;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Order createOrder(User user, UUID productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("Product not found with id: " + productId));

        if (!product.isActive()) {
            throw new ApiException("Product is not available");
        }

        if (product.getStock() < quantity) {
            throw new ApiException("Insufficient stock. Available: " + product.getStock());
        }

        Order order = new Order(user, product, quantity, product.getPrice() * quantity);
        order.setOrderNumber(generateOrderNumber());

        Order savedOrder = orderRepository.save(order);

        // Update product stock
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        return savedOrder;
    }

    @Override
    public Order getOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order not found with id: " + id));
    }

    @Override
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ApiException("Order not found with order number: " + orderNumber));
    }

    @Override
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order updateOrderStatus(UUID orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found with id: " + orderId));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found with id: " + orderId));

        if ("CANCELLED".equals(order.getStatus()) || "DELIVERED".equals(order.getStatus())) {
            throw new ApiException("Cannot cancel order with status: " + order.getStatus());
        }

        // Restore product stock
        Product product = order.getProduct();
        product.setStock(product.getStock() + order.getQuantity());
        productRepository.save(product);

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

