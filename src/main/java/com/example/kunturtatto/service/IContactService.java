package com.example.kunturtatto.service;

public interface IContactService {
    
    void sendEmail( String email, String subject, String message, String tattooCm, String body, String linksReference); 

    void sendMailRemainder();
}
