package com.example.kunturtatto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.kunturtatto.model.Contact;
import com.example.kunturtatto.service.IContactService;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/mail")
@PreAuthorize("permitAll()")
public class MailController {
    
    @Autowired
    private IContactService contactService;

    /**
     * Maneja el envío de un formulario de contacto.
     * @param contact Objeto que contiene la información del contacto.
     * @param model Modelo para pasar atributos a la vista.
     * @return La plantilla de la página de contacto.
     */
    @PostMapping("/contacto/guardar")
    public String saveContact(@ModelAttribute Contact contact, Model model) {
        // Llamar al servicio para enviar el correo
        contactService.sendEmail(
            contact.getEmail(), 
            contact.getSubject(), 
            contact.getMessage(), 
            contact.getTattooCm(), 
            contact.getBody(), 
            contact.getLinksReference()
        );

        // Añadir un atributo para indicar que el mensaje se ha enviado
        model.addAttribute("mensajeEnviado", true);
        
        // Redirigir a la vista de contacto con confirmación de envío
        return "user/contact";
    }
}
