package com.example.kunturtatto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kunturtatto.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>{
    
}
