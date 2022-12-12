package com.neverdroid.ecoflow.bot.job;

import com.neverdroid.ecoflow.bot.model.QueryDeviceQuota;
import com.neverdroid.ecoflow.bot.service.Bot;
import com.neverdroid.ecoflow.bot.service.EcoFlow;
import com.neverdroid.ecoflow.bot.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component

public class Scheduler {

    @Value("${chartId}")
    String chartId;

    @Value("${deviceId}")
    String deviceId;

    private final Bot bot;
    private final EcoFlow ecoFlow;

    private final AtomicBoolean isRunningOut = new AtomicBoolean(false);
    private final AtomicBoolean isCharged = new AtomicBoolean(false);
    private final AtomicBoolean isCharging = new AtomicBoolean(false);
    private final AtomicBoolean isSocketInOff = new AtomicBoolean(false);

    public Scheduler(Bot bot, EcoFlow ecoFlow) {
        this.bot = bot;
        this.ecoFlow = ecoFlow;
    }

    @Scheduled(cron = "*/60 * * * * *")
    public void scheduleTask() {

        QueryDeviceQuota deviceQuota = ecoFlow.getDeviceQuota(deviceId);
        if (deviceQuota == null) {
            log.info("Device quota is null");
            return;
        }

        log.info("Battery Soc: " + deviceQuota.getData().getSoc());

        if (!isCharged.get() && deviceQuota.getData().getWattsInSum().equals(deviceQuota.getData().getWattsOutSum()) && deviceQuota.getData().getRemainTime() == 5999) {
            isCharged.set(true);

            isRunningOut.set(false);
            isCharging.set(false);
            isSocketInOff.set(false);

            sendMessage("<b>Battery is charged</b> \n\n" + MessageUtil.getStatusTelegramMessage(deviceQuota));
            return;
        }

        if (!isCharging.get() && deviceQuota.getData().getWattsInSum() > deviceQuota.getData().getWattsOutSum()) {
            isCharging.set(true);

            isRunningOut.set(false);
            isCharged.set(false);
            isSocketInOff.set(false);

            sendMessage("<b>Battery is charging</b> \n\n" + MessageUtil.getStatusTelegramMessage(deviceQuota));
            return;
        }


        if (!isRunningOut.get() && deviceQuota.getData().getSoc() <= 20 && deviceQuota.getData().getWattsInSum() == 0) {
            isRunningOut.set(true);

            isCharged.set(false);
            isCharging.set(false);
            isSocketInOff.set(false);

            sendMessage("<b>Battery run out of charge in "+deviceQuota.getData().getRemainTime()+" minutes</b> \n\n" + MessageUtil.getStatusTelegramMessage(deviceQuota));
            return;
        }

        if (!isSocketInOff.get() && deviceQuota.getData().getWattsInSum() == 0) {
            isSocketInOff.set(true);

            isRunningOut.set(false);
            isCharged.set(false);
            isCharging.set(false);

            sendMessage("<b>Battery socket in off</b> \n\n" + MessageUtil.getStatusTelegramMessage(deviceQuota));
        }
    }

    private void sendMessage(String telegramMessage) {
        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder();
        messageBuilder.chatId(chartId);
        messageBuilder.parseMode(ParseMode.HTML);

        messageBuilder.text(telegramMessage);
        try {
            bot.execute(messageBuilder.build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

}
