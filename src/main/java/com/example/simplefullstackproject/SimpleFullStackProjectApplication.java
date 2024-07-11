package com.example.simplefullstackproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimpleFullStackProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleFullStackProjectApplication.class, args);
    }

}
