package com.example.kunturtatto.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.kunturtatto.service.IContactService;

@Component
public class MailScheduler {

    @Autowired
    private IContactService contactService;

    @Scheduled(cron = "0 0 9 * * ?") // Corre todos los días a las 9 AM
    public void sendDailyAppointmentReminder() {
        contactService.sendMailRemainder();
    }
}
