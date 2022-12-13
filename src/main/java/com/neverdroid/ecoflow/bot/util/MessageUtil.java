package com.neverdroid.ecoflow.bot.util;

import com.neverdroid.ecoflow.bot.model.QueryDeviceQuota;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MessageUtil {
    public static String getStatusTelegramMessage(QueryDeviceQuota queryDeviceQuota ) {

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Europe/Kiev"));


        return "<b>executed at -" + now + "</b> \n\n" +
                "Soc % - " + queryDeviceQuota.getData().getSoc() + "\n" +
                "Remain Time - " + queryDeviceQuota.getData().getRemainTime() + "\n" +
                "Watts In Sum - " + queryDeviceQuota.getData().getWattsInSum() + "  \n" +
                "Watts Out Sum - " + queryDeviceQuota.getData().getWattsOutSum() + "\n";
    }
}
