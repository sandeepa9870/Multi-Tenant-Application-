package com.example.multitenant.service;

import com.example.multitenant.dto.BrandRequest;
import com.example.multitenant.dto.BrandResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BrandService {
    BrandResponse createBrand(BrandRequest request);
    BrandResponse updateBrand(Long id, BrandRequest request);
    BrandResponse getBrand(Long id);
    Page<BrandResponse> getAllBrands(Pageable pageable);
    Page<BrandResponse> searchBrands(String name, Pageable pageable);
    void deleteBrand(Long id);
}

