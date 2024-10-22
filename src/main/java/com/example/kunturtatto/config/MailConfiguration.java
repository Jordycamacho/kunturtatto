package com.example.kunturtatto.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableScheduling
public class MailConfiguration {
    
    @Value("${email.sender}")
    private String myEmail;
    @Bean
    public JavaMailSender getJavaMailSender(){
        

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(myEmail);
        mailSender.setPassword("kggi bsqn lpps brzw");

        Properties props = mailSender.getJavaMailProperties();

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
