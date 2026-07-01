package com.example.multitenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingMethodResponse {
    private Long id;
    private String shippingName;
    private Integer deliveryTime;
    private Double shippingCost;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

