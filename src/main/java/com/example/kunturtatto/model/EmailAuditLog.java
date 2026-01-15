package com.example.kunturtatto.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "audit_id", nullable = false, unique = true)
    private String auditId;
    
    @Column(name = "email_type", nullable = false)
    private String emailType;
    
    @Column(name = "sender", nullable = false)
    private String sender;
    
    @Column(name = "recipient", nullable = false)
    private String recipient;
    
    @Column(name = "subject", nullable = false, length = 500)
    private String subject;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "execution_time")
    private Long executionTime;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    @Column(name = "appointment_id")
    private Long appointmentId;
    
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
    
    @Column(name = "application_name")
    private String applicationName;
    
    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }
}