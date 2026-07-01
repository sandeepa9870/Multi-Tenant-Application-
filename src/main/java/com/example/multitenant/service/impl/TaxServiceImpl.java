package com.example.multitenant.service.impl;

import com.example.multitenant.dto.TaxRequest;
import com.example.multitenant.dto.TaxResponse;
import com.example.multitenant.entity.Tax;
import com.example.multitenant.exception.ApiException;
import com.example.multitenant.mapper.TaxMapper;
import com.example.multitenant.repository.TaxRepository;
import com.example.multitenant.service.TaxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class TaxServiceImpl implements TaxService {
    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private TaxMapper taxMapper;

    @Override
    public TaxResponse createTax(TaxRequest request) {
        log.info("Creating tax: {}", request.getTaxName());

        if (taxRepository.existsByTaxName(request.getTaxName())) {
            throw new ApiException("Tax with name '" + request.getTaxName() + "' already exists");
        }

        Tax tax = taxMapper.toEntity(request);
        tax = taxRepository.save(tax);

        log.info("Tax created successfully with id: {}", tax.getId());
        return taxMapper.toResponse(tax);
    }

    @Override
    public TaxResponse updateTax(Long id, TaxRequest request) {
        log.info("Updating tax with id: {}", id);

        Tax tax = taxRepository.findById(id)
            .orElseThrow(() -> new ApiException("Tax not found with id: " + id));

        if (!tax.getTaxName().equals(request.getTaxName()) && taxRepository.existsByTaxName(request.getTaxName())) {
            throw new ApiException("Tax with name '" + request.getTaxName() + "' already exists");
        }

        taxMapper.updateEntity(request, tax);
        tax.setUpdatedAt(LocalDateTime.now());
        tax = taxRepository.save(tax);

        log.info("Tax updated successfully with id: {}", id);
        return taxMapper.toResponse(tax);
    }

    @Override
    public TaxResponse getTax(Long id) {
        log.info("Fetching tax with id: {}", id);

        Tax tax = taxRepository.findById(id)
            .orElseThrow(() -> new ApiException("Tax not found with id: " + id));

        return taxMapper.toResponse(tax);
    }

    @Override
    public Page<TaxResponse> getAllTaxes(Pageable pageable) {
        log.info("Fetching all taxes with pagination");

        return taxRepository.findAll(pageable)
            .map(taxMapper::toResponse);
    }

    @Override
    public void deleteTax(Long id) {
        log.info("Deleting tax with id: {}", id);

        if (!taxRepository.existsById(id)) {
            throw new ApiException("Tax not found with id: " + id);
        }

        taxRepository.deleteById(id);
        log.info("Tax deleted successfully with id: {}", id);
    }
}

