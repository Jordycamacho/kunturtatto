package com.example.kunturtatto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.kunturtatto.model.Contact;
import com.example.kunturtatto.service.IContactService;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/mail")
@Component
public class MailController {
    
    @Autowired
    private IContactService contactService;
    
    @PostMapping("/contacto/guardar")
    public String saveContact(@ModelAttribute Contact contact, Model model) {
        contactService.sendEmail(contact.getEmail(), contact.getSubject(), contact.getMessage(), 
                                 contact.getTattooCm(), contact.getBody(), contact.getLinksReference());
        model.addAttribute("mensajeEnviado", true);
        return "user/contact";
    }
    

    @PostMapping("/recordatorio")
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendAppointmentReminder(){
        contactService.sendMailRemainder();
    }
}
