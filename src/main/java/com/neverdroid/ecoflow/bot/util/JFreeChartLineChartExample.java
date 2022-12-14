package com.neverdroid.ecoflow.bot.util;

import com.neverdroid.ecoflow.bot.model.Data;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

public class JFreeChartLineChartExample {
    public static void main(String[] args) throws IOException {

        String[] headers = new String[]{"Date", "soc", "remainTime", "wattsOutSum", "wattsInSum"};
        Reader in = new FileReader("DeviceQuota-DAEBZ5ZD9241160-2022-12-14.csv");

        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(headers)
                .withFirstRecordAsHeader()
                .parse(in);

        final XYSeries wattsOutSumSeries = new XYSeries("Watts Out Sum");
        final XYSeries wattsInSumSeries = new XYSeries("Watts In Sum");


        ArrayList<Data> dataList = new ArrayList<>();
        HashSet<Integer> hours = new HashSet<>();

        for (CSVRecord record : records) {

            String date = record.get("Date");
            date = "2022-12-14T" + date;

            LocalDateTime parse = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            hours.add(parse.getHour());

            dataList.add(new Data(
                    Integer.parseInt(record.get("soc")),
                    Integer.parseInt(record.get("remainTime")),
                    Integer.parseInt(record.get("wattsOutSum")),
                    Integer.parseInt(record.get("wattsInSum")),
                    parse
            ));
        }


        for (Integer hour : hours) {
            OptionalDouble averageWattsOutSum = dataList.stream().filter(data -> data.getDateTime().getHour() == hour).mapToInt(Data::getWattsOutSum).average();
            OptionalDouble averageWattsInSum = dataList.stream().filter(data -> data.getDateTime().getHour() == hour).mapToInt(Data::getWattsInSum).average();

            double hourDouble = hour;
            wattsOutSumSeries.add(hourDouble, averageWattsOutSum.getAsDouble());
            wattsInSumSeries.add(hourDouble,  averageWattsInSum.getAsDouble());
        }

        long countOff = dataList.stream().filter(data -> data.getWattsInSum() == 0).count();
        System.out.println("minutes without socket power = " + countOff);

        long countOn = dataList.stream().filter(data -> data.getWattsInSum() != 0).count();
        System.out.println("minutes with socket power = " + countOn);

        OptionalDouble averageOut = dataList.stream().mapToInt(Data::getWattsOutSum).average();
        System.out.println("average watts out = " + averageOut.getAsDouble());

        OptionalInt wattsOutMax = dataList.stream().mapToInt(Data::getWattsOutSum).max();
        System.out.println("wattsOutMax = " + wattsOutMax.getAsInt());

        OptionalInt wattsInMax = dataList.stream().mapToInt(Data::getWattsInSum).max();
        System.out.println("wattsInMax = " + wattsInMax.getAsInt());

        OptionalDouble averageIn = dataList.stream()
                .filter(data -> data.getWattsInSum() != 0)
                .mapToInt(Data::getWattsInSum).average();

        System.out.println("average watts in = " + averageIn.getAsDouble());

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeriesCollection.addSeries(wattsOutSumSeries);
        xySeriesCollection.addSeries(wattsInSumSeries);

        JFreeChart pieChart = ChartFactory.createXYLineChart("Power supply report", "time", "watts", xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        ChartUtils.saveChartAsPNG(new File("histogram1.png"), pieChart, 450, 400);

    }
}
