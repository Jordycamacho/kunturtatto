package com.example.kunturtatto.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="appointment")
@Getter
@Setter
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAppointment;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String time;
    private Double price;

    private String customerEmail;

    private String design;
    private String tattooCm; 
    private String LinkReference;
    private String body;
    private String message;
    
}
