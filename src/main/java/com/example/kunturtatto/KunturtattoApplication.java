package com.example.kunturtatto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KunturtattoApplication {

        public static void main(String[] args) {
                SpringApplication.run(KunturtattoApplication.class, args);
        }
}
