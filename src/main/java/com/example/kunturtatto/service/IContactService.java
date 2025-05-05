package com.example.kunturtatto.service;

import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.request.ContactRequest;

public interface IContactService {
    
    void sendContactEmail(ContactRequest request); 
    void sendAppointmentUpdateNotification(Appointment updatedAppointment);
    void sendAppointmentConfirmation(Appointment appointment);
    void sendAppointmentCancellation(Appointment appointment);
}
