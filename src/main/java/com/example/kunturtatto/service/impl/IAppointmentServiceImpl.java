package com.example.kunturtatto.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.repository.AppointmentRepository;
import com.example.kunturtatto.service.IAppointmentService;

/**
 * Implementación del servicio para gestionar las operaciones relacionadas con
 * citas.
 */
@Service
public class IAppointmentServiceImpl implements IAppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    /**
     * Obtiene todas las citas.
     * 
     * @return Lista de todas las citas
     */
    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    /**
     * Guarda una nueva cita.
     * 
     * @param appointment Objeto Appointment a guardar
     * @return Cita guardada
     */
    @Override
    @Transactional
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    /**
     * Actualiza una cita existente.
     * 
     * @param appointment Objeto Appointment a actualizar
     * @return Cita actualizada
     */
    @Override
    @Transactional
    public Appointment update(Appointment appointment) {
        if (appointment.getIdAppointment() == null
                || !appointmentRepository.existsById(appointment.getIdAppointment())) {
            throw new IllegalArgumentException("La cita no existe o el ID es inválido");
        }
        return appointmentRepository.save(appointment);
    }

    /**
     * Elimina una cita por su ID.
     * 
     * @param idAppointment ID de la cita a eliminar
     */
    @Override
    @Transactional
    public void deleteById(Long idAppointment) {
        if (!appointmentRepository.existsById(idAppointment)) {
            throw new IllegalArgumentException("La cita no existe o el ID es inválido");
        }
        appointmentRepository.deleteById(idAppointment);
    }

    /**
     * Encuentra una cita por su ID.
     * 
     * @param idAppointment ID de la cita a buscar
     * @return Cita encontrada o null si no existe
     */
    @Override
    @Transactional(readOnly = true)
    public Appointment findById(Long idAppointment) {
        return appointmentRepository.findById(idAppointment).orElse(null);
    }

    /**
     * Obtiene las citas del día actual.
     * 
     * @return Lista de citas para el día actual
     */
    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getTodaysAppointments() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 2024-10-17T00:00
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX); // 2024-10-17T23:59:59.999999999

        return appointmentRepository.findByDateBetween(startOfDay, endOfDay);
    }
}
