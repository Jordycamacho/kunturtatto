package com.example.kunturtatto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.kunturtatto.model.enums.AppointmentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppointmentResponse {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDate date;
    private String time;
    private Double price;
    private AppointmentStatus status;
    private String designTitle;
    private Double tattooSize;
    private String bodyPart;
    private String referenceLinks;
    private String customDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
