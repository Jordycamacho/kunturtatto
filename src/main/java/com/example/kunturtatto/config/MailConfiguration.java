package com.example.kunturtatto.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MailConfiguration {

    @Value("${email.sender}")
    private String myEmail;
    @Value("${email.password}")
    private String myPasswordEmail;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(myEmail);
        mailSender.setPassword(myPasswordEmail);

        Properties props = mailSender.getJavaMailProperties();

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.localhost", "kunturtattoo.com");
        props.put("mail.smtp.helo", "kunturtattoo.com");

        return mailSender;
    }
}
