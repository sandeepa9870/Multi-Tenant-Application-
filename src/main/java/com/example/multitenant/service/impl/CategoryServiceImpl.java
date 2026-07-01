package com.example.multitenant.service.impl;

import com.example.multitenant.dto.CategoryRequest;
import com.example.multitenant.dto.CategoryResponse;
import com.example.multitenant.entity.Category;
import com.example.multitenant.exception.ApiException;
import com.example.multitenant.mapper.CategoryMapper;
import com.example.multitenant.repository.CategoryRepository;
import com.example.multitenant.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating category: {}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            throw new ApiException("Category with name '" + request.getName() + "' already exists");
        }

        Category category = categoryMapper.toEntity(request);
        category = categoryRepository.save(category);

        log.info("Category created successfully with id: {}", category.getId());
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("Updating category with id: {}", id);

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ApiException("Category not found with id: " + id));

        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new ApiException("Category with name '" + request.getName() + "' already exists");
        }

        categoryMapper.updateEntity(request, category);
        category.setUpdatedAt(LocalDateTime.now());
        category = categoryRepository.save(category);

        log.info("Category updated successfully with id: {}", id);
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse getCategory(Long id) {
        log.info("Fetching category with id: {}", id);

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ApiException("Category not found with id: " + id));

        return categoryMapper.toResponse(category);
    }

    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        log.info("Fetching all categories with pagination");

        return categoryRepository.findAll(pageable)
            .map(categoryMapper::toResponse);
    }

    @Override
    public Page<CategoryResponse> searchCategories(String name, Pageable pageable) {
        log.info("Searching categories with name: {}", name);

        return categoryRepository.findByNameContainingIgnoreCase(name, pageable)
            .map(categoryMapper::toResponse);
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);

        if (!categoryRepository.existsById(id)) {
            throw new ApiException("Category not found with id: " + id);
        }

        categoryRepository.deleteById(id);
        log.info("Category deleted successfully with id: {}", id);
    }

    @Override
    public void enableCategory(Long id) {
        log.info("Enabling category with id: {}", id);

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ApiException("Category not found with id: " + id));

        category.setStatus(true);
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);

        log.info("Category enabled successfully with id: {}", id);
    }

    @Override
    public void disableCategory(Long id) {
        log.info("Disabling category with id: {}", id);

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ApiException("Category not found with id: " + id));

        category.setStatus(false);
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);

        log.info("Category disabled successfully with id: {}", id);
    }
}

