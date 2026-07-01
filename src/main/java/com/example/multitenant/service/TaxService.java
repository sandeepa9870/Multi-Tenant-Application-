package com.example.multitenant.service;

import com.example.multitenant.dto.TaxRequest;
import com.example.multitenant.dto.TaxResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaxService {
    TaxResponse createTax(TaxRequest request);
    TaxResponse updateTax(Long id, TaxRequest request);
    TaxResponse getTax(Long id);
    Page<TaxResponse> getAllTaxes(Pageable pageable);
    void deleteTax(Long id);
}

