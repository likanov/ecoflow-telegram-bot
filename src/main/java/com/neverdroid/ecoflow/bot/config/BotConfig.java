package com.neverdroid.ecoflow.bot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("classpath:application.properties")
public class BotConfig {
    // Имя бота заданное при регистрации
    @Value("${botUserName}")
    String botUserName;

    // Токен полученный при регистрации
    @Value("${token}")
    String token;
}
