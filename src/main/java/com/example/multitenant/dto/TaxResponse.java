package com.example.multitenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxResponse {
    private Long id;
    private String taxName;
    private Double percentage;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

