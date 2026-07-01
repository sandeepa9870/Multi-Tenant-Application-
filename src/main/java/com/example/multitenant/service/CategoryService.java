package com.example.multitenant.service;

import com.example.multitenant.dto.CategoryRequest;
import com.example.multitenant.dto.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    CategoryResponse getCategory(Long id);
    Page<CategoryResponse> getAllCategories(Pageable pageable);
    Page<CategoryResponse> searchCategories(String name, Pageable pageable);
    void deleteCategory(Long id);
    void enableCategory(Long id);
    void disableCategory(Long id);
}

