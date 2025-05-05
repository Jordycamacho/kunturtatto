package com.example.kunturtatto.components;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.kunturtatto.service.RemainderService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class MailScheduler {
    private final RemainderService remainderService;

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyAppointmentReminder() {
        remainderService.sendMailRemainder();
    }
}
