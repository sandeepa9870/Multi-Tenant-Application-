package com.example.multitenant.service.impl;

import com.example.multitenant.dto.AuditLogResponse;
import com.example.multitenant.entity.AuditLog;
import com.example.multitenant.mapper.AuditLogMapper;
import com.example.multitenant.repository.AuditLogRepository;
import com.example.multitenant.service.AuditLogService;
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
public class AuditLogServiceImpl implements AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Override
    public void logAction(String userEmail, String action, String module, String ipAddress) {
        log.info("Logging action - User: {}, Action: {}, Module: {}", userEmail, action, module);

        AuditLog auditLog = new AuditLog(userEmail, action, module, ipAddress);
        auditLogRepository.save(auditLog);
    }

    @Override
    public void logAction(String userEmail, String action, String module, String details, String ipAddress) {
        log.info("Logging action with details - User: {}, Action: {}, Module: {}", userEmail, action, module);

        AuditLog auditLog = new AuditLog(userEmail, action, module, details, ipAddress);
        auditLogRepository.save(auditLog);
    }

    @Override
    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {
        log.info("Fetching audit logs with pagination");

        return auditLogRepository.findAll(pageable)
            .map(auditLogMapper::toResponse);
    }

    @Override
    public Page<AuditLogResponse> getAuditLogsByUser(String userEmail, Pageable pageable) {
        log.info("Fetching audit logs for user: {}", userEmail);

        return auditLogRepository.findByUserEmail(userEmail, pageable)
            .map(auditLogMapper::toResponse);
    }

    @Override
    public Page<AuditLogResponse> getAuditLogsByModule(String module, Pageable pageable) {
        log.info("Fetching audit logs for module: {}", module);

        return auditLogRepository.findByModule(module, pageable)
            .map(auditLogMapper::toResponse);
    }

    @Override
    public Page<AuditLogResponse> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.info("Fetching audit logs between {} and {}", startDate, endDate);

        return auditLogRepository.findByTimestampBetween(startDate, endDate, pageable)
            .map(auditLogMapper::toResponse);
    }
}

