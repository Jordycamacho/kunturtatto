package com.example.kunturtatto.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.kunturtatto.model.enums.AppointmentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String customerName;
    
    @NotBlank @Email
    private String customerEmail;
    
    @NotBlank
    private String customerPhone;
    
    @NotNull @FutureOrPresent
    private LocalDate date;
    
    @NotBlank
    private String time;
    
    @NotNull @Positive
    private Double price;
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    
    @ManyToOne
    private Design design;
    
    @Positive
    private Double tattooSize;
    
    @NotBlank
    private String bodyPart;
    
    private String referenceLinks;
    
    @Size(max = 1000)
    private String customDescription;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}