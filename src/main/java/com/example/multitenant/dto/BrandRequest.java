package com.example.multitenant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class BrandRequest {
    @NotBlank(message = "Brand name is required")
    @Size(min = 2, max = 100, message = "Brand name must be between 2 and 100 characters")
    private String name;

    private String logo;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}

