package com.example.multitenant.repository;

import com.example.multitenant.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByName(String name);
    boolean existsByName(String name);
    Page<Brand> findByStatusTrue(Pageable pageable);
    Page<Brand> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

