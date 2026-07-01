package com.example.multitenant.mapper;

import com.example.multitenant.dto.BrandRequest;
import com.example.multitenant.dto.BrandResponse;
import com.example.multitenant.entity.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {
    public Brand toEntity(BrandRequest request) {
        return new Brand(request.getName(), request.getLogo(), request.getDescription());
    }

    public BrandResponse toResponse(Brand brand) {
        return new BrandResponse(
            brand.getId(),
            brand.getName(),
            brand.getLogo(),
            brand.getDescription(),
            brand.getStatus(),
            brand.getCreatedAt(),
            brand.getUpdatedAt()
        );
    }

    public void updateEntity(BrandRequest request, Brand brand) {
        brand.setName(request.getName());
        brand.setLogo(request.getLogo());
        brand.setDescription(request.getDescription());
    }
}

