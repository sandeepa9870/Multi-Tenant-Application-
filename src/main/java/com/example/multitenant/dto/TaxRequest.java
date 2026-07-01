package com.example.multitenant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxRequest {
    @NotBlank(message = "Tax name is required")
    private String taxName;

    @NotNull(message = "Tax percentage is required")
    @Min(value = 0, message = "Tax percentage must be greater than or equal to 0")
    private Double percentage;
}

