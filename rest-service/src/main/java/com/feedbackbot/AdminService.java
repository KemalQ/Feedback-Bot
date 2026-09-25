package com.feedbackbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AdminService {
    public static void main(String[] args) {

        SpringApplication.run(AdminService.class, args);
    }
}