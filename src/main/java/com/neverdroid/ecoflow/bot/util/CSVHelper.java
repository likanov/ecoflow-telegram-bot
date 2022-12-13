package com.neverdroid.ecoflow.bot.util;

import com.neverdroid.ecoflow.bot.model.QueryDeviceQuota;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CSVHelper {
    public static void putDeviceQuota2csv(QueryDeviceQuota deviceQuota, String deviceId) {

        String dateFileName = OffsetDateTime.now(ZoneId.of("Europe/Kiev")).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String date4DateInCsv = OffsetDateTime.now(ZoneId.of("Europe/Kiev")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).substring(11, 19);
        String outputFileName = "DeviceQuota-" + deviceId + "-" + dateFileName + ".csv";

        String[] headers = new String[]{ "Date", "soc", "remainTime", "wattsOutSum", "wattsInSum"};

        boolean isFileExists = new File(outputFileName).exists();
        CSVPrinter csvPrinter;
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFileName), StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            if (!isFileExists) {
                csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
            } else {
                csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            }
            csvPrinter.printRecord(date4DateInCsv, deviceQuota.getData().getSoc(), deviceQuota.getData().getRemainTime(), deviceQuota.getData().getWattsOutSum(), deviceQuota.getData().getWattsInSum());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
