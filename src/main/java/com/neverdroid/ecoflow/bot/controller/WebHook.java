package com.neverdroid.ecoflow.bot.controller;

import com.google.gson.Gson;
import com.neverdroid.ecoflow.bot.service.Bot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RestController
@RequestMapping("/api/public/gitea")
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class WebHook {
    Bot bot;

    // Канал в который будем слать уведомления
    @Value("${chartId}")
    String chartId;

    // Секретный ключ который придёт в нутри JSON от Gitea,
    // что бы левые люди не имели доступа к боту т.к. API публичное без авторизации
    @Value("${secret}")
    String secret;

    @Autowired
    public WebHook(Bot bot) {
        this.bot = bot;
    }

    @PostMapping(value = "/webhook")
    public ResponseEntity<?> webhook(@RequestBody String json) {

        Gson gson = new Gson();

        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder();
        messageBuilder.chatId(chartId);

        messageBuilder.parseMode(ParseMode.HTML);
        StringBuilder builder = new StringBuilder();
        builder.append("<b>hello from controller</b> ");


        messageBuilder.text(builder.toString());
        try {
            bot.execute(messageBuilder.build());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }


}
