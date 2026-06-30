package com.example.multitenant.controller;

import com.example.multitenant.entity.Product;
import com.example.multitenant.service.ProductService;
import com.example.multitenant.util.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/products")
public class PublicProductController {

    private final ProductService productService;

    public PublicProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllActiveProducts() {
        List<Product> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(ApiResponse.of("Active products retrieved successfully", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable java.util.UUID id) {
        Product product = productService.getProductById(id);
        if (!product.isActive()) {
            return ResponseEntity.status(404).body(ApiResponse.error("Product not found"));
        }
        return ResponseEntity.ok(ApiResponse.of("Product retrieved successfully", product));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(ApiResponse.of("Products retrieved by category", products));
    }
}

