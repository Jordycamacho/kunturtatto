package com.example.kunturtatto.service;

import com.example.kunturtatto.model.EmailAuditLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EmailAuditService {
    
    List<EmailAuditLog> getEmailAuditLogs(LocalDateTime startDate, LocalDateTime endDate);
    
    List<EmailAuditLog> getFailedEmails(LocalDateTime since);
    
    Map<String, Long> getEmailStatistics(LocalDateTime since);
    
    Long getSuccessRate(LocalDateTime since);
    
    List<EmailAuditLog> getEmailsByAppointment(Long appointmentId);
    
    void cleanupOldLogs(int daysToKeep);
}