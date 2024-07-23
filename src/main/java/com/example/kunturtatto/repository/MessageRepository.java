package com.example.kunturtatto.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kunturtatto.model.Message;
import com.example.kunturtatto.model.User;

public interface MessageRepository extends JpaRepository <Message, Long>{
    
    List<Message> findByUser(User user);
    
}
