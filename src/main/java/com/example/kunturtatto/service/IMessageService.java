package com.example.kunturtatto.service;

import java.util.List;

import com.example.kunturtatto.model.Message;
import com.example.kunturtatto.model.User;

public interface IMessageService {

    List<Message> findAll();
    List<Message> findByUser(User user);
    Message save(Message message);
    void delete(Long idMessage);

    
}
