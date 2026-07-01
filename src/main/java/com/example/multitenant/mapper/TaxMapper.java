package com.example.multitenant.mapper;

import com.example.multitenant.dto.TaxRequest;
import com.example.multitenant.dto.TaxResponse;
import com.example.multitenant.entity.Tax;
import org.springframework.stereotype.Component;

@Component
public class TaxMapper {
    public Tax toEntity(TaxRequest request) {
        return new Tax(request.getTaxName(), request.getPercentage());
    }

    public TaxResponse toResponse(Tax tax) {
        return new TaxResponse(
            tax.getId(),
            tax.getTaxName(),
            tax.getPercentage(),
            tax.getStatus(),
            tax.getCreatedAt(),
            tax.getUpdatedAt()
        );
    }

    public void updateEntity(TaxRequest request, Tax tax) {
        tax.setTaxName(request.getTaxName());
        tax.setPercentage(request.getPercentage());
    }
}

