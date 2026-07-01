package com.example.multitenant.mapper;

import com.example.multitenant.dto.AuditLogResponse;
import com.example.multitenant.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {
    public AuditLogResponse toResponse(AuditLog auditLog) {
        return new AuditLogResponse(
            auditLog.getId(),
            auditLog.getUserEmail(),
            auditLog.getAction(),
            auditLog.getModule(),
            auditLog.getDetails(),
            auditLog.getTimestamp(),
            auditLog.getIpAddress()
        );
    }
}

