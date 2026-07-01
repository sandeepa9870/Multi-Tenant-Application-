package com.example.multitenant.service.impl;

import com.example.multitenant.dto.ShippingMethodRequest;
import com.example.multitenant.dto.ShippingMethodResponse;
import com.example.multitenant.entity.ShippingMethod;
import com.example.multitenant.exception.ApiException;
import com.example.multitenant.mapper.ShippingMethodMapper;
import com.example.multitenant.repository.ShippingMethodRepository;
import com.example.multitenant.service.ShippingMethodService;
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
public class ShippingMethodServiceImpl implements ShippingMethodService {
    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    @Autowired
    private ShippingMethodMapper shippingMethodMapper;

    @Override
    public ShippingMethodResponse createShippingMethod(ShippingMethodRequest request) {
        log.info("Creating shipping method: {}", request.getShippingName());

        if (shippingMethodRepository.existsByShippingName(request.getShippingName())) {
            throw new ApiException("Shipping method with name '" + request.getShippingName() + "' already exists");
        }

        ShippingMethod shippingMethod = shippingMethodMapper.toEntity(request);
        shippingMethod = shippingMethodRepository.save(shippingMethod);

        log.info("Shipping method created successfully with id: {}", shippingMethod.getId());
        return shippingMethodMapper.toResponse(shippingMethod);
    }

    @Override
    public ShippingMethodResponse updateShippingMethod(Long id, ShippingMethodRequest request) {
        log.info("Updating shipping method with id: {}", id);

        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
            .orElseThrow(() -> new ApiException("Shipping method not found with id: " + id));

        if (!shippingMethod.getShippingName().equals(request.getShippingName()) &&
            shippingMethodRepository.existsByShippingName(request.getShippingName())) {
            throw new ApiException("Shipping method with name '" + request.getShippingName() + "' already exists");
        }

        shippingMethodMapper.updateEntity(request, shippingMethod);
        shippingMethod.setUpdatedAt(LocalDateTime.now());
        shippingMethod = shippingMethodRepository.save(shippingMethod);

        log.info("Shipping method updated successfully with id: {}", id);
        return shippingMethodMapper.toResponse(shippingMethod);
    }

    @Override
    public ShippingMethodResponse getShippingMethod(Long id) {
        log.info("Fetching shipping method with id: {}", id);

        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
            .orElseThrow(() -> new ApiException("Shipping method not found with id: " + id));

        return shippingMethodMapper.toResponse(shippingMethod);
    }

    @Override
    public Page<ShippingMethodResponse> getAllShippingMethods(Pageable pageable) {
        log.info("Fetching all shipping methods with pagination");

        return shippingMethodRepository.findAll(pageable)
            .map(shippingMethodMapper::toResponse);
    }

    @Override
    public void deleteShippingMethod(Long id) {
        log.info("Deleting shipping method with id: {}", id);

        if (!shippingMethodRepository.existsById(id)) {
            throw new ApiException("Shipping method not found with id: " + id);
        }

        shippingMethodRepository.deleteById(id);
        log.info("Shipping method deleted successfully with id: {}", id);
    }
}

