package com.example.kunturtatto.service;

import java.util.List;

import com.example.kunturtatto.model.Appointment;

public interface IAppointmentService {
    List<Appointment> findAll();
    List<Appointment> getTodaysAppointments();
    Appointment save(Appointment  appointment);
    Appointment update(Appointment  appointment);
    Appointment findById(Long idAppointment);
    void deleteById(Long idAppointment);
}
