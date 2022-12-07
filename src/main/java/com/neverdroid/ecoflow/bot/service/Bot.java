package com.neverdroid.ecoflow.bot.service;

import com.neverdroid.ecoflow.bot.config.BotConfig;
import com.neverdroid.ecoflow.bot.model.QueryDeviceQuota;
import com.neverdroid.ecoflow.bot.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    final BotConfig config;

    @Value("${chartId}")
    String chartId;

    @Value("${deviceId}")
    String deviceId;
    @Autowired
    private EcoFlow ecoFlow;
    public Bot(BotConfig config) {
        this.config = config;
    }

    public void onUpdateReceived(Update update) {
        update.getUpdateId();
        SendMessage.SendMessageBuilder builder = SendMessage.builder();
        String messageText;
        String chatId;
        if (update.getMessage() != null) {
            chatId = update.getMessage().getChatId().toString();
            builder.chatId(chatId);
            messageText = update.getMessage().getText();
        } else {
            chatId = update.getChannelPost().getChatId().toString();
            builder.chatId(chatId);
            messageText = update.getChannelPost().getText();
        }

        if (messageText.contains("/command2")) {
            builder.text("hello there general Kenobi");
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }

        if (messageText.contains("/command1")) {
            builder.text("chart ID: " + chatId);
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }

        if (messageText.contains("/command3") && chatId.equals(chartId)) {
            QueryDeviceQuota deviceQuota = ecoFlow.getDeviceQuota(deviceId);
            builder.parseMode(ParseMode.HTML);
            builder.text(MessageUtil.getTelegramMessage(deviceQuota));
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }


    public String getBotUsername() {
        return config.getBotUserName();
    }

    public String getBotToken() {
        return config.getToken();
    }
}