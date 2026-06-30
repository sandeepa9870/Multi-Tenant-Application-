package com.example.multitenant.service;

import com.example.multitenant.entity.Product;
import java.util.UUID;
import java.util.List;

public interface ProductService {
    Product createProduct(String name, String description, Double price, Integer stock, String sku, String category);
    Product updateProduct(UUID id, String name, String description, Double price, Integer stock, String category);
    void deleteProduct(UUID id);
    Product getProductById(UUID id);
    List<Product> getAllActiveProducts();
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);
    void updateStock(UUID productId, Integer quantity);
}

