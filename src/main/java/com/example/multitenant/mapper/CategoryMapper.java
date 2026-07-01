package com.example.multitenant.mapper;

import com.example.multitenant.dto.CategoryRequest;
import com.example.multitenant.dto.CategoryResponse;
import com.example.multitenant.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public Category toEntity(CategoryRequest request) {
        return new Category(request.getName(), request.getDescription(), request.getImageUrl());
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getImageUrl(),
            category.getStatus(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }

    public void updateEntity(CategoryRequest request, Category category) {
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
    }
}

