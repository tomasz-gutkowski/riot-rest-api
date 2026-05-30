package com.rra.project.riotrestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RiotApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiotApiApplication.class, args);
    }

}
