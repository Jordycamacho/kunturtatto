package com.example.kunturtatto.repository;

import com.example.kunturtatto.model.EmailAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailAuditLogRepository extends JpaRepository<EmailAuditLog, Long> {
    
    List<EmailAuditLog> findByEmailType(String emailType);
    
    List<EmailAuditLog> findByStatus(String status);
    
    List<EmailAuditLog> findByRecipient(String recipient);
    
    List<EmailAuditLog> findByAppointmentId(Long appointmentId);
    
    @Query("SELECT e FROM EmailAuditLog e WHERE e.sentAt BETWEEN :startDate AND :endDate")
    List<EmailAuditLog> findBySentAtBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(e) FROM EmailAuditLog e WHERE e.status = :status AND e.sentAt >= :since")
    Long countByStatusSince(@Param("status") String status, 
                           @Param("since") LocalDateTime since);
    
    @Query("SELECT e.emailType, COUNT(e) FROM EmailAuditLog e GROUP BY e.emailType")
    List<Object[]> countByEmailType();
}