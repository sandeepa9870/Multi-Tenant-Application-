package com.example.multitenant.repository;

import com.example.multitenant.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByUserEmail(String userEmail, Pageable pageable);
    Page<AuditLog> findByModule(String module, Pageable pageable);
    Page<AuditLog> findByAction(String action, Pageable pageable);
    Page<AuditLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<AuditLog> findByUserEmailAndModuleAndTimestampBetween(String userEmail, String module, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}

