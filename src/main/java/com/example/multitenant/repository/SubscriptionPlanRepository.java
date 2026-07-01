package com.example.multitenant.repository;

import com.example.multitenant.entity.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findByPlanName(String planName);
    boolean existsByPlanName(String planName);
    Page<SubscriptionPlan> findByStatusTrue(Pageable pageable);
}

