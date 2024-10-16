package com.example.kunturtatto.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kunturtatto.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    /**
     * Encuentra todas las citas para una fecha específica.
     * 
     * @param date Fecha para la cual se desean buscar citas
     * @return Lista de citas que ocurren en la fecha especificada
     */
    List<Appointment> findByDate(LocalDate date);
}