package com.rahul.outboxorderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OutboxOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OutboxOrderServiceApplication.class, args);
    }

}
