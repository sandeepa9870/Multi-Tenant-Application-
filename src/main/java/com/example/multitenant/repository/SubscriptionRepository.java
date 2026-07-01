package com.example.multitenant.repository;

import com.example.multitenant.entity.ShippingMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
    Optional<ShippingMethod> findByShippingName(String shippingName);
    boolean existsByShippingName(String shippingName);
    Page<ShippingMethod> findByStatusTrue(Pageable pageable);
}

