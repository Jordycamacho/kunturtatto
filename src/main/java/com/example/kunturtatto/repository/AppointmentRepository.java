package com.example.kunturtatto.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kunturtatto.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>{
    
    List<Appointment> findByDate(LocalDate date);

}