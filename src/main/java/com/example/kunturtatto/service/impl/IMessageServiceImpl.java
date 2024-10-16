package com.example.kunturtatto.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kunturtatto.model.Message;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.repository.MessageRepository;
import com.example.kunturtatto.service.IMessageService;

/**
 * Servicio para gestionar operaciones de mensajes.
 */
@Service
public class IMessageServiceImpl implements IMessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    @Transactional
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public void delete(Long idMessage) {
        if (!messageRepository.existsById(idMessage)) {
            throw new IllegalArgumentException("El mensaje con ID " + idMessage + " no existe.");
        }
        messageRepository.deleteById(idMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> findByUser(User user) {
        return messageRepository.findByUser(user);
    }
}
