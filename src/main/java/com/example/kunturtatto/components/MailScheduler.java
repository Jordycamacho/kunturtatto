package com.example.kunturtatto.components;

import org.springframework.stereotype.Component;
import com.example.kunturtatto.service.RemainderService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class MailScheduler {
    private final RemainderService remainderService;

    public void sendDailyAppointmentReminder() {
        remainderService.sendMailRemainder();
    }
}
