package com.neverdroid.ecoflow.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@AutoConfigurationPackage
@EnableScheduling
public class EcoflowBotApplication {

    public static void main(String[] args) {
		SpringApplication.run(EcoflowBotApplication.class, args);
    }

}
