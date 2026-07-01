package com.example.multitenant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingMethodRequest {
    @NotBlank(message = "Shipping method name is required")
    private String shippingName;

    @NotNull(message = "Delivery time is required")
    @Min(value = 1, message = "Delivery time must be at least 1 day")
    private Integer deliveryTime;

    @NotNull(message = "Shipping cost is required")
    @Min(value = 0, message = "Shipping cost must be greater than or equal to 0")
    private Double shippingCost;
}
