package com.neverdroid.ecoflow.bot.util;

import com.neverdroid.ecoflow.bot.model.QueryDeviceQuota;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class MessageUtil {
    public static String getStatusTelegramMessage(QueryDeviceQuota queryDeviceQuota ) {

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Europe/Kiev"));

        return "<b>executed at -" + now + "</b> \n\n" +
                "Code - " + queryDeviceQuota.getCode() + " \n" +
                "Message - " + queryDeviceQuota.getMessage() + "\n\n" +
                "Soc % - " + queryDeviceQuota.getData().getSoc() + "\n" +
                "Remain Time - " + queryDeviceQuota.getData().getRemainTime() + "\n" +
                "Watts In Sum - " + queryDeviceQuota.getData().getWattsInSum() + "  \n" +
                "Watts Out Sum - " + queryDeviceQuota.getData().getWattsOutSum() + "\n";
    }
}
