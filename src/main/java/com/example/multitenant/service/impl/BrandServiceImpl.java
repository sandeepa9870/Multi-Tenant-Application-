package com.example.multitenant.service.impl;

import com.example.multitenant.dto.BrandRequest;
import com.example.multitenant.dto.BrandResponse;
import com.example.multitenant.entity.Brand;
import com.example.multitenant.exception.ApiException;
import com.example.multitenant.mapper.BrandMapper;
import com.example.multitenant.repository.BrandRepository;
import com.example.multitenant.service.BrandService;
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
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public BrandResponse createBrand(BrandRequest request) {
        log.info("Creating brand: {}", request.getName());

        if (brandRepository.existsByName(request.getName())) {
            throw new ApiException("Brand with name '" + request.getName() + "' already exists");
        }

        Brand brand = brandMapper.toEntity(request);
        brand = brandRepository.save(brand);

        log.info("Brand created successfully with id: {}", brand.getId());
        return brandMapper.toResponse(brand);
    }

    @Override
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        log.info("Updating brand with id: {}", id);

        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new ApiException("Brand not found with id: " + id));

        if (!brand.getName().equals(request.getName()) && brandRepository.existsByName(request.getName())) {
            throw new ApiException("Brand with name '" + request.getName() + "' already exists");
        }

        brandMapper.updateEntity(request, brand);
        brand.setUpdatedAt(LocalDateTime.now());
        brand = brandRepository.save(brand);

        log.info("Brand updated successfully with id: {}", id);
        return brandMapper.toResponse(brand);
    }

    @Override
    public BrandResponse getBrand(Long id) {
        log.info("Fetching brand with id: {}", id);

        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new ApiException("Brand not found with id: " + id));

        return brandMapper.toResponse(brand);
    }

    @Override
    public Page<BrandResponse> getAllBrands(Pageable pageable) {
        log.info("Fetching all brands with pagination");

        return brandRepository.findAll(pageable)
            .map(brandMapper::toResponse);
    }

    @Override
    public Page<BrandResponse> searchBrands(String name, Pageable pageable) {
        log.info("Searching brands with name: {}", name);

        return brandRepository.findByNameContainingIgnoreCase(name, pageable)
            .map(brandMapper::toResponse);
    }

    @Override
    public void deleteBrand(Long id) {
        log.info("Deleting brand with id: {}", id);

        if (!brandRepository.existsById(id)) {
            throw new ApiException("Brand not found with id: " + id);
        }

        brandRepository.deleteById(id);
        log.info("Brand deleted successfully with id: {}", id);
    }
}

