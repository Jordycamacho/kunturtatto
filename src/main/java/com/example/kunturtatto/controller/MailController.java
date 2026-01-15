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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequestMapping("/mail")
@Component
@Tag(name = "Gestión de Correos", description = "Operaciones para el envío de correos electrónicos del sistema")
public class MailController {

    @Autowired
    private IContactService contactService;

    @Operation(summary = "Enviar correo de contacto", description = "Procesa el formulario de contacto y envía un correo electrónico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correo enviado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del formulario inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al enviar el correo")
    })
    @PostMapping("/contacto/guardar")
    public String saveContact(
            @Parameter(description = "Datos del formulario de contacto", required = true) 
            @Valid @ModelAttribute ContactRequest request,
            BindingResult bindingResult,
            Model model) {
        
        log.info("[POST /mail/contacto/guardar] Procesando envío de correo de contacto. Datos: email={}, asunto={}", 
                request.getEmail(), request.getSubject());

        if (bindingResult.hasErrors()) {
            log.error("[POST /mail/contacto/guardar] Errores de validación en formulario de contacto: {}", 
                    bindingResult.getAllErrors());
            return "user/contact";
        }

        try {
            long startTime = System.currentTimeMillis();
            contactService.sendContactEmail(request);
            long endTime = System.currentTimeMillis();
            
            log.info("[POST /mail/contacto/guardar] Correo de contacto enviado exitosamente en {} ms", 
                    (endTime - startTime));
            model.addAttribute("mensajeEnviado", true);
            
        } catch (MailException e) {
            log.error("[POST /mail/contacto/guardar] Error al enviar correo de contacto: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al enviar el mensaje. Por favor, intente nuevamente.");
        } catch (Exception e) {
            log.error("[POST /mail/contacto/guardar] Error inesperado al enviar correo de contacto: {}", 
                    e.getMessage(), e);
            model.addAttribute("error", "Error inesperado. Por favor, contacte al administrador.");
        }

        return "user/contact";
    }
}