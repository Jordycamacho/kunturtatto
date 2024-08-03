package com.example.kunturtatto.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.service.IContactService;

@Service
public class IContactServieImpl implements IContactService{

    @Value("${email.sender}")
    private String myEmail;
    
    @Autowired
    private JavaMailSender javaMailSender;

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
    
}
