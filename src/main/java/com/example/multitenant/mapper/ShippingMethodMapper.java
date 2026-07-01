package com.example.multitenant.mapper;

import com.example.multitenant.dto.ShippingMethodRequest;
import com.example.multitenant.dto.ShippingMethodResponse;
import com.example.multitenant.entity.ShippingMethod;
import org.springframework.stereotype.Component;

@Component
public class ShippingMethodMapper {
    public ShippingMethod toEntity(ShippingMethodRequest request) {
        return new ShippingMethod(request.getShippingName(), request.getDeliveryTime(), request.getShippingCost());
    }

    public ShippingMethodResponse toResponse(ShippingMethod shippingMethod) {
        return new ShippingMethodResponse(
            shippingMethod.getId(),
            shippingMethod.getShippingName(),
            shippingMethod.getDeliveryTime(),
            shippingMethod.getShippingCost(),
            shippingMethod.getStatus(),
            shippingMethod.getCreatedAt(),
            shippingMethod.getUpdatedAt()
        );
    }

    public void updateEntity(ShippingMethodRequest request, ShippingMethod shippingMethod) {
        shippingMethod.setShippingName(request.getShippingName());
        shippingMethod.setDeliveryTime(request.getDeliveryTime());
        shippingMethod.setShippingCost(request.getShippingCost());
    }
}

