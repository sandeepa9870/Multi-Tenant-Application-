package com.example.multitenant.repository;

import com.example.multitenant.entity.Tax;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
    Optional<Tax> findByTaxName(String taxName);
    boolean existsByTaxName(String taxName);
    Page<Tax> findByStatusTrue(Pageable pageable);
}

