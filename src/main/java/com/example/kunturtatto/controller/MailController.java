package com.example.kunturtatto.controller;

import com.example.kunturtatto.dtos.ContactDTO;
import com.example.kunturtatto.service.IContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mail")
public class MailController {

    private final IContactService contactService;

    private static final Logger logger = LoggerFactory.getLogger(MailController.class);

    public MailController(IContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Maneja el envío de un formulario de contacto y envía un correo electrónico.
     *
     * @param contactDTO Objeto DTO que contiene la información del contacto.
     * @param model      Modelo para pasar atributos a la vista.
     * @return La plantilla de la página de contacto.
     */
    @Operation(summary = "Guardar contacto y enviar correo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "El correo se envió correctamente"),
        @ApiResponse(responseCode = "500", description = "Error al enviar el correo")
    })
    @PostMapping("/contacto/guardar")
    public String saveContact(@ModelAttribute ContactDTO contactDTO, Model model) {
        try {
            // Llamada al servicio de envío de correo electrónico
            contactService.sendEmail(
                contactDTO.getEmail(),
                contactDTO.getSubject(),
                contactDTO.getMessage(),
                contactDTO.getTattooCm(),
                contactDTO.getBody(),
                contactDTO.getLinksReference()
            );

            logger.info("Correo enviado con éxito a: {}", contactDTO.getEmail());

            model.addAttribute("mensajeEnviado", true);

        } catch (Exception e) {
            logger.error("Error al enviar correo a {}: {}", contactDTO.getEmail(), e.getMessage(), e);
            model.addAttribute("mensajeError", "No se pudo enviar el correo. Por favor, intente de nuevo.");
        }

        // Redirige a la vista de contacto
        return "user/contact";
    }
}
