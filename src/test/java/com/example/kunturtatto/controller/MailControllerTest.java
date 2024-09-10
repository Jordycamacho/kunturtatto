package com.example.kunturtatto.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.kunturtatto.config.TestSecurityConfig;
import com.example.kunturtatto.service.IContactService;


@WebMvcTest(MailController.class)
@Import(TestSecurityConfig.class)
public class MailControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IContactService contactService;

    @Test
    public void testSaveContact() throws Exception {
        // Simular el comportamiento del servicio de contacto
        doNothing().when(contactService).sendEmail(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());

        // Cuerpo de la solicitud como parámetros de formulario
        mockMvc.perform(post("/mail/contacto/guardar")
                .param("email", "test@gmail.com")
                .param("subject", "Test Subject")
                .param("message", "Test Message")
                .param("tattooCm", "10")
                .param("body", "Espalda")
                .param("linksReference", "https://example.com/reference")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("user/contact")) 
                .andExpect(model().attributeExists("mensajeEnviado")) 
                .andExpect(model().attribute("mensajeEnviado", true)); 

        // Verificar que el servicio de contacto fue llamado con los parámetros correctos
        verify(contactService, times(1)).sendEmail(
                eq("test@gmail.com"),
                eq("Test Subject"),
                eq("Test Message"),
                eq("10"),
                eq("Espalda"),
                eq("https://example.com/reference")
        );
    }

}
