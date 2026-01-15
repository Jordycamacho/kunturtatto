package com.example.kunturtatto.service.impl;

import com.example.kunturtatto.model.EmailAuditLog;
import com.example.kunturtatto.repository.EmailAuditLogRepository;
import com.example.kunturtatto.service.EmailAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAuditServiceImpl implements EmailAuditService {
    
    private final EmailAuditLogRepository emailAuditLogRepository;
    
    @Override
    public List<EmailAuditLog> getEmailAuditLogs(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("[EMAIL_AUDIT_SERVICE] Obteniendo logs de auditoría entre {} y {}", startDate, endDate);
        return emailAuditLogRepository.findBySentAtBetween(startDate, endDate);
    }
    
    @Override
    public List<EmailAuditLog> getFailedEmails(LocalDateTime since) {
        log.debug("[EMAIL_AUDIT_SERVICE] Obteniendo emails fallidos desde {}", since);
        List<EmailAuditLog> failedEmails = emailAuditLogRepository.findByStatus("FAILED");
        return failedEmails.stream()
                .filter(email -> email.getSentAt().isAfter(since))
                .toList();
    }
    
    @Override
    public Map<String, Long> getEmailStatistics(LocalDateTime since) {
        log.debug("[EMAIL_AUDIT_SERVICE] Obteniendo estadísticas desde {}", since);
        
        Map<String, Long> statistics = new HashMap<>();
        
        Long total = emailAuditLogRepository.countByStatusSince("SUCCESS", since) +
                     emailAuditLogRepository.countByStatusSince("FAILED", since);
        
        Long success = emailAuditLogRepository.countByStatusSince("SUCCESS", since);
        Long failed = emailAuditLogRepository.countByStatusSince("FAILED", since);
        
        statistics.put("TOTAL", total);
        statistics.put("SUCCESS", success);
        statistics.put("FAILED", failed);
        
        List<Object[]> typeCounts = emailAuditLogRepository.countByEmailType();
        for (Object[] count : typeCounts) {
            statistics.put((String) count[0], (Long) count[1]);
        }
        
        log.info("[EMAIL_AUDIT_SERVICE] Estadísticas obtenidas: total={}, exitosos={}, fallidos={}", 
                total, success, failed);
        
        return statistics;
    }
    
    @Override
    public Long getSuccessRate(LocalDateTime since) {
        Long total = emailAuditLogRepository.countByStatusSince("SUCCESS", since) +
                     emailAuditLogRepository.countByStatusSince("FAILED", since);
        
        if (total == 0) {
            return 100L;
        }
        
        Long success = emailAuditLogRepository.countByStatusSince("SUCCESS", since);
        return (success * 100) / total;
    }
    
    @Override
    public List<EmailAuditLog> getEmailsByAppointment(Long appointmentId) {
        log.debug("[EMAIL_AUDIT_SERVICE] Obteniendo emails para la cita {}", appointmentId);
        return emailAuditLogRepository.findByAppointmentId(appointmentId);
    }
    
    @Override
    @Transactional
    public void cleanupOldLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        List<EmailAuditLog> oldLogs = emailAuditLogRepository.findBySentAtBetween(
            LocalDateTime.MIN, cutoffDate
        );
        
        if (!oldLogs.isEmpty()) {
            emailAuditLogRepository.deleteAll(oldLogs);
            log.info("[EMAIL_AUDIT_SERVICE] Eliminados {} logs de auditoría antiguos", oldLogs.size());
        }
    }
}