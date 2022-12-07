package com.neverdroid.ecoflow.bot.job;

import com.neverdroid.ecoflow.bot.model.QueryDeviceQuota;
import com.neverdroid.ecoflow.bot.service.Bot;
import com.neverdroid.ecoflow.bot.service.EcoFlow;
import com.neverdroid.ecoflow.bot.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component

public class Scheduler {
    @Autowired
    Bot bot;
    @Value("${chartId}")
    String chartId;

    @Value("${deviceId}")
    String deviceId;

    @Autowired
    private EcoFlow ecoFlow;

    private final AtomicBoolean isUsersNotified = new AtomicBoolean(false);

    @Scheduled(cron = "*/60 * * * * *")
    public void scheduleTask() {

        QueryDeviceQuota deviceQuota = ecoFlow.getDeviceQuota(deviceId);
        if(deviceQuota == null){
            log.info("Device quota is null");
            return;
        }

        if(deviceQuota.getData().getSoc() >= 20){
            log.info("Device quota Soc is: " + deviceQuota.getData().getSoc());
            isUsersNotified.set(false);
            return;
        }

        if(isUsersNotified.get()){
            log.info("Users already notified");
            return;
        }

        if(!deviceQuota.getData().getWattsInSum().equals(0)){
            log.info("Device is charging");
            return;
        }

        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder();
        messageBuilder.chatId(chartId);
        messageBuilder.parseMode(ParseMode.HTML);
        messageBuilder.text(MessageUtil.getTelegramMessage(deviceQuota));
        try {
            bot.execute(messageBuilder.build());
            isUsersNotified.set(true);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

}
