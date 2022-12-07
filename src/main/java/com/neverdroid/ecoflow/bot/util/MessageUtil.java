package com.neverdroid.ecoflow.bot.util;

import com.neverdroid.ecoflow.bot.model.QueryDeviceQuota;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageUtil {
    public static String getTelegramMessage(QueryDeviceQuota queryDeviceQuota ) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String strDate = dateFormat.format(new Date());

        return "<b>running at -" + strDate + "</b> \n\n" +
                "Code - " + queryDeviceQuota.getCode() + " \n" +
                "Message - " + queryDeviceQuota.getMessage() + "\n\n" +
                "Soc % - " + queryDeviceQuota.getData().getSoc() + "\n" +
                "Remain Time - " + queryDeviceQuota.getData().getRemainTime() + "\n" +
                "Watts In Sum - " + queryDeviceQuota.getData().getWattsInSum() + "  \n" +
                "Watts Out Sum - " + queryDeviceQuota.getData().getWattsOutSum() + "\n";
    }
}
