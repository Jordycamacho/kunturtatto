package com.example.kunturtatto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.kunturtatto.request.ContactRequest;
import com.example.kunturtatto.service.IContactService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/mail")
@Component
public class MailController {

    @Autowired
    private IContactService contactService;

    @PostMapping("/contacto/guardar")
    public String saveContact(@Valid @ModelAttribute ContactRequest request,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "user/contact";
        }

        try {
            contactService.sendContactEmail(request);
            model.addAttribute("mensajeEnviado", true);
        } catch (MailException e) {
            model.addAttribute("error", "Error al enviar el mensaje");
        }

        return "user/contact";
    }

}
