package com.example.multitenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantCategoryResponse {
    private Long id;
    private TenantResponse tenant;
    private CategoryResponse category;
    private LocalDateTime assignedAt;
    private String assignedBy;
    private Boolean active;
}

