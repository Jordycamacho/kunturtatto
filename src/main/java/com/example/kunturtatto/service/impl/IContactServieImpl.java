package com.example.kunturtatto.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.model.Appointment;
import com.example.kunturtatto.service.IAppointmentService;
import com.example.kunturtatto.service.IContactService;

@Service
public class IContactServieImpl implements IContactService{

    @Value("${email.sender}")
    private String myEmail;
    
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private IAppointmentService appointmentService;

    @Override
    public void sendEmail(String email, String subject, String message, String tattooCm, String body, String linksReference) {
        
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        String formarMessage = "\nEnviado por: " + email + 
                               "\n\n\nDescripción del tatuaje:" + 
                               "\n\nTamaño en cemtimetros: " + tattooCm + 
                               "\nParte del cuerpo: " + body + 
                               "\nLinks de referecia: " + linksReference + 
                               "\nmensaje: " +  message ;

        try {
            simpleMailMessage.setFrom(myEmail);
            simpleMailMessage.setTo(myEmail);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(formarMessage);

            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMailRemainder(){

        List<Appointment> todayAppointments = appointmentService.getTodaysAppointments();
        if (todayAppointments.isEmpty()) {
            return;
        }

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        String subject = "Citas del Día";

        StringBuilder formatMessage = new StringBuilder("Las citas que tienes pendientes para hoy son:\n\n");
        for (Appointment appointment : todayAppointments) {
            formatMessage.append("\nFecha: ").append(appointment.getDate())
                         .append("\nHora: ").append(appointment.getTime())
                         .append("\nEmail del Cliente: ").append(appointment.getCustomerEmail())
                         .append("\n\n");
        }
        
        try {
            simpleMailMessage.setFrom(myEmail);
            simpleMailMessage.setTo(myEmail);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(formatMessage.toString());

            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
