package com.example.kunturtatto.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
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
    private String email;
    private String message;
    private String body;
    private String tattooCm; 
    private String LinkReference;
    private Double price;
    private Date messageDate;
    

    @ManyToOne
    @JoinColumn(name = "idUser")
    private User user;
}
