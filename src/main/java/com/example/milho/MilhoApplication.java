package com.example.milho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync


public class MilhoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MilhoApplication.class, args);
    }
}

