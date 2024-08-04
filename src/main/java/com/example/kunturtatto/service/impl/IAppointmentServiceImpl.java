package com.example.kunturtatto.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.repository.AppointmentRepository;
import com.example.kunturtatto.service.IAppointmentService;

@Service
public class IAppointmentServiceImpl implements IAppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository;
    @Override
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment update(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Override
    public void deleteById(Long idAppointment) {
        appointmentRepository.deleteById(idAppointment);
    }

    @Override
    public Appointment findById(Long idAppointment) {
        return appointmentRepository.findById(idAppointment).orElse(null);
    }
    
}
