package com.example.kunturtatto.service;

import java.util.List;

import com.example.kunturtatto.model.Message;
import com.example.kunturtatto.model.User;

/**
 * Servicio para gestionar mensajes.
 */
public interface IMessageService {

    /**
     * Obtiene todos los mensajes.
     * 
     * @return Lista de todos los mensajes
     */
    List<Message> findAll();

    /**
     * Encuentra mensajes enviados por un usuario específico.
     * 
     * @param user Usuario que envió los mensajes
     * @return Lista de mensajes asociados al usuario especificado
     */
    List<Message> findByUser(User user);

    /**
     * Guarda un mensaje nuevo.
     * 
     * @param message Objeto del mensaje a guardar
     * @return Mensaje guardado
     */
    Message save(Message message);

    /**
     * Elimina un mensaje por su ID.
     * 
     * @param idMessage ID del mensaje a eliminar
     */
    void delete(Long idMessage);
}
