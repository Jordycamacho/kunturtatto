package com.example.kunturtatto.service;

/**
 * Servicio para manejar operaciones relacionadas con el contacto y envío de correos electrónicos.
 */
public interface IContactService {

    /**
     * Envía un correo electrónico con detalles específicos.
     * 
     * @param email Dirección de correo del destinatario
     * @param subject Asunto del correo
     * @param message Mensaje del correo
     * @param tattooCm Tamaño del tatuaje
     * @param body Parte del cuerpo donde se colocará el tatuaje
     * @param linksReference Enlaces de referencia para el diseño del tatuaje
     */
    void sendEmail(String email, String subject, String message, String tattooCm, String body, String linksReference);

    /**
     * Envía recordatorios por correo electrónico.
     */
    void sendMailRemainder();
}

