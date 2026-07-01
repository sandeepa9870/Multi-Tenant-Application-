package com.example.multitenant.service;

import com.example.multitenant.dto.ShippingMethodRequest;
import com.example.multitenant.dto.ShippingMethodResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShippingMethodService {
    ShippingMethodResponse createShippingMethod(ShippingMethodRequest request);
    ShippingMethodResponse updateShippingMethod(Long id, ShippingMethodRequest request);
    ShippingMethodResponse getShippingMethod(Long id);
    Page<ShippingMethodResponse> getAllShippingMethods(Pageable pageable);
    void deleteShippingMethod(Long id);
}

