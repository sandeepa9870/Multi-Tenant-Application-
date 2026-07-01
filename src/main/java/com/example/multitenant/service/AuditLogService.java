package com.example.multitenant.service;

import com.example.multitenant.dto.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuditLogService {
    void logAction(String userEmail, String action, String module, String ipAddress);
    void logAction(String userEmail, String action, String module, String details, String ipAddress);
    Page<AuditLogResponse> getAuditLogs(Pageable pageable);
    Page<AuditLogResponse> getAuditLogsByUser(String userEmail, Pageable pageable);
    Page<AuditLogResponse> getAuditLogsByModule(String module, Pageable pageable);
    Page<AuditLogResponse> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}

