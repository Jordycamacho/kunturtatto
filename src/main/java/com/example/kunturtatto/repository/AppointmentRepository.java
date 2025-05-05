package com.example.kunturtatto.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.model.enums.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByDate(LocalDate date);
    
    List<Appointment> findByDateOrderByTimeAsc(LocalDate date);
    
    List<Appointment> findByDateGreaterThanEqualOrderByDateAscTimeAsc(LocalDate date);
    
    List<Appointment> findAllByOrderByDateAscTimeAsc();
    
    List<Appointment> findByStatus(AppointmentStatus status);
}