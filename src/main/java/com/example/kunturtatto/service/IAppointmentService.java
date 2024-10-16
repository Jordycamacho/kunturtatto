package com.example.kunturtatto.service;

import java.util.List;

import com.example.kunturtatto.model.Appointment;

/**
 * Servicio para gestionar las operaciones relacionadas con las citas.
 */
public interface IAppointmentService {

    /**
     * Obtiene todas las citas.
     * 
     * @return Lista de todas las citas
     */
    List<Appointment> findAll();

    /**
     * Obtiene las citas del día actual.
     * 
     * @return Lista de citas para el día actual
     */
    List<Appointment> getTodaysAppointments();

    /**
     * Guarda una nueva cita.
     * 
     * @param appointment Objeto Appointment a guardar
     * @return Cita guardada
     */
    Appointment save(Appointment appointment);

    /**
     * Actualiza una cita existente.
     * 
     * @param appointment Objeto Appointment a actualizar
     * @return Cita actualizada
     */
    Appointment update(Appointment appointment);

    /**
     * Encuentra una cita por su ID.
     * 
     * @param idAppointment ID de la cita a buscar
     * @return Cita encontrada
     */
    Appointment findById(Long idAppointment);

    /**
     * Elimina una cita por su ID.
     * 
     * @param idAppointment ID de la cita a eliminar
     */
    void deleteById(Long idAppointment);
}

