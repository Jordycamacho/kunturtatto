package com.example.kunturtatto.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kunturtatto.model.Message;
import com.example.kunturtatto.model.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * Encuentra mensajes enviados por un usuario específico.
     * 
     * @param user Objeto User para filtrar mensajes
     * @return Lista de mensajes asociados al usuario especificado
     */
    List<Message> findByUser(User user);
}
