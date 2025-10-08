package com.example.kunturtatto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement 
public class KunturtattoApplication {

        public static void main(String[] args) {
                SpringApplication.run(KunturtattoApplication.class, args);
        }
}
