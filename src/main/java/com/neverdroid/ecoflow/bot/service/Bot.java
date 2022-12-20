package com.neverdroid.ecoflow.bot.service;

import com.neverdroid.ecoflow.bot.config.BotConfig;
import com.neverdroid.ecoflow.bot.model.QueryDeviceQuota;
import com.neverdroid.ecoflow.bot.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final EcoFlow ecoFlow;

    private final BuildProperties buildProperties;

    @Value("${chartId}")
    String chartId;

    @Value("${deviceId}")
    String deviceId;

    public Bot(BotConfig config, EcoFlow ecoFlow, BuildProperties buildProperties) {
        this.config = config;
        this.ecoFlow = ecoFlow;
        this.buildProperties = buildProperties;
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

        if ((messageText.contains("/command3") || messageText.contains("status")) && chatId.equals(chartId)) {
            QueryDeviceQuota deviceQuota = ecoFlow.getDeviceQuota(deviceId);
            builder.parseMode(ParseMode.HTML);
            builder.text(MessageUtil.getStatusTelegramMessage(deviceQuota));
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }

        if (messageText.contains("build info") && chatId.equals(chartId)) {
            builder.parseMode(ParseMode.HTML);
            String buildInfo = new StringBuilder().append("Version: ").append(buildProperties.getVersion()).append("\n").
                    append("Name: ").append(buildProperties.getName()).append("\n").
                    append("Time: ").append(buildProperties.getTime()).append("\n").
                    append("Group: ").append(buildProperties.getGroup()).append("\n").
                    append("Git branch: ").append(buildProperties.get("git.branch")).append("\n").
                    append("Git commit: ").append(buildProperties.get("git.commit")).append("\n").
                    append("Git URL: ").append(buildProperties.get("git.url")).append("\n").
                    append("Artifact: ").append(buildProperties.getArtifact()).toString();

            builder.text(buildInfo);
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }

        if (messageText.contains("/menu") && chatId.equals(chartId)) {
            builder.parseMode(ParseMode.HTML);
            String message = "Please select an option from below:" ;
            builder.text(message);
            SendMessage sendMessage = builder.build();
            setButtons(sendMessage);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }

        if (messageText.contains("12 hours report") && chatId.equals(chartId)) {
            try {
                SendPhoto sendPhoto = SendPhoto.builder()
                        .chatId(chartId)
                        .photo(new InputFile(new FileInputStream(new File("PDFBarChartDemo1.pdf")), "PDFBarChartDemo1.pdf"))
                        .caption("12 hours report")
                        .build();
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public synchronized void setButtons(SendMessage sendMessage) {
        // Create a keyboard
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Create a list of keyboard rows
        List<KeyboardRow> keyboard = new ArrayList<>();

        // First keyboard row
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        // Add buttons to the first keyboard row
        keyboardFirstRow.add(new KeyboardButton("status"));
        keyboardFirstRow.add(new KeyboardButton("12 hours report"));

        // Second keyboard row
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        // Add the buttons to the second keyboard row
        keyboardSecondRow.add(new KeyboardButton("help"));
        keyboardSecondRow.add(new KeyboardButton("get build info"));

        // Add all of the keyboard rows to the list
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);

        // and assign this list to our keyboard
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public String getBotUsername() {
        return config.getBotUserName();
    }

    public String getBotToken() {
        return config.getToken();
    }
}