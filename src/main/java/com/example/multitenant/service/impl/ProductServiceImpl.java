package com.example.multitenant.service.impl;

import com.example.multitenant.entity.Product;
import com.example.multitenant.exception.ApiException;
import com.example.multitenant.repository.ProductRepository;
import com.example.multitenant.service.ProductService;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(String name, String description, Double price, Integer stock, String sku, String category) {
        if (productRepository.existsBySku(sku)) {
            throw new ApiException("Product with SKU already exists: " + sku);
        }
        Product product = new Product(name, description, price, stock, sku, category);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(UUID id, String name, String description, Double price, Integer stock, String category) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found with id: " + id));

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found with id: " + id));
    }

    @Override
    public List<Product> getAllActiveProducts() {
        return productRepository.findAllByActive(true);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public void updateStock(UUID productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("Product not found with id: " + productId));

        if (product.getStock() < quantity) {
            throw new ApiException("Insufficient stock. Available: " + product.getStock());
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}

