package com.pin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PINManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PINManagerApplication.class, args);
    }

}