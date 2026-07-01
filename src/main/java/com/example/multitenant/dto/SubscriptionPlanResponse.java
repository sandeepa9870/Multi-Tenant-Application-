package com.example.multitenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanResponse {
    private Long id;
    private String planName;
    private Integer duration;
    private Double price;
    private String features;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

