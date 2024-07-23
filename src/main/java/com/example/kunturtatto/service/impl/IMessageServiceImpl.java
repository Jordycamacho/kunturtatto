package com.example.kunturtatto.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.model.Message;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.repository.MessageRepository;
import com.example.kunturtatto.service.IMessageService;

@Service
public class IMessageServiceImpl implements IMessageService{

    @Autowired
    private MessageRepository messageRepository;
    
    @Override
    public List<Message> findAll() {

        return messageRepository.findAll();
    }

    @Override
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public void delete(Long idMessage) {
        messageRepository.deleteById(idMessage);
    }

    @Override
    public List<Message> findByUser(User user) {
        
        return messageRepository.findByUser(user);
    }
    
}
