package com.example.kunturtatto.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class MessageDTO {
    private Long idMessage;
    private String content;
    private Date messageDate;
    private Long userId;
}

