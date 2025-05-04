package com.example.kunturtatto.service;

import com.example.kunturtatto.request.ContactRequest;

public interface IContactService {
    
    void sendContactEmail(ContactRequest request); 

    void sendMailRemainder();
}
