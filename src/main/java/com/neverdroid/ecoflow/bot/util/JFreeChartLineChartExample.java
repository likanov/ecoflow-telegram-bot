package com.neverdroid.ecoflow.bot.util;

import com.neverdroid.ecoflow.bot.model.Data;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.pdf.PDFDocument;
import org.jfree.pdf.PDFGraphics2D;
import org.jfree.pdf.Page;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

public class JFreeChartLineChartExample {
    public static void main(String[] args) throws IOException {

        String[] headers = new String[]{"Date", "soc", "remainTime", "wattsOutSum", "wattsInSum"};
        String currentDate = OffsetDateTime.now(ZoneId.of("Europe/Kiev")).format(DateTimeFormatter.ISO_LOCAL_DATE);

        OffsetDateTime.now(ZoneId.of("Europe/Kiev")).minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

        Reader in = new FileReader("DeviceQuota-DAEBZ5ZD9241160-"+currentDate+".csv");

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
            date = currentDate+"T" + date;

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

        double sumConsumedWatts = 0;

        DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();


        for (Integer hour : hours) {
            OptionalDouble averageWattsOutSum = dataList.stream().filter(data -> data.getDateTime().getHour() == hour).mapToInt(Data::getWattsOutSum).average();
            int[] wattsOutSumArray = dataList.stream().filter(data -> data.getDateTime().getHour() == hour).mapToInt(Data::getWattsOutSum).toArray();
            double outSumStandardDeviation = getStandardDeviation(wattsOutSumArray);

            OptionalDouble averageWattsInSum = dataList.stream().filter(data -> data.getDateTime().getHour() == hour).mapToInt(Data::getWattsInSum).average();
            int[] wattsInSumArray = dataList.stream().filter(data -> data.getDateTime().getHour() == hour).mapToInt(Data::getWattsInSum).toArray();
            double inSumStandardDeviation = getStandardDeviation(wattsInSumArray);

            double hourDouble = hour;
            dataset.add(averageWattsOutSum.getAsDouble(), outSumStandardDeviation, "WattsOutSum", String.valueOf(hourDouble));
            dataset.add(averageWattsInSum.getAsDouble(), inSumStandardDeviation, "WattsInSum", String.valueOf(hourDouble));

            sumConsumedWatts += averageWattsOutSum.getAsDouble();
        }

        System.out.println("sumConsumedWatts = " + sumConsumedWatts);

        long countOff = dataList.stream().filter(data -> data.getWattsInSum() == 0).count();
        System.out.println("minutes without socket power = " + countOff);

        long countOn = dataList.stream().filter(data -> data.getWattsInSum() != 0).count();
        System.out.println("minutes with socket power = " + countOn);


        int sum = dataList.stream().filter(data -> data.getWattsInSum() == 0).mapToInt(Data::getWattsOutSum).sum();
        System.out.println("sum consumed watts = " + sum);

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

        JFreeChart chart = createChart(dataset);
        PDFDocument pdfDoc = new PDFDocument();
        pdfDoc.setTitle("PDFBarChartDemo2");
        pdfDoc.setAuthor("neverdroid.com");
        Page page = pdfDoc.createPage(new Rectangle(612, 468));
        PDFGraphics2D g2 = page.getGraphics2D();
        chart.draw(g2, new Rectangle(0, 0, 612, 468));
        pdfDoc.writeToFile(new File("PDFBarChartDemo2.pdf"));
    }


    private static JFreeChart createChart(CategoryDataset dataset) {

        // create the chart...
        JFreeChart chart = ChartFactory.createLineChart( "Power supply report", "Hour", "watts", dataset);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        // customise the range axis...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(false);

        // customise the renderer...
        StatisticalBarRenderer renderer = new StatisticalBarRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setErrorIndicatorPaint(Color.BLACK);
        renderer.setIncludeBaseInRange(false);
        plot.setRenderer(renderer);

        // ensure the current theme is applied to the renderer just added
        ChartUtils.applyCurrentTheme(chart);

        renderer.setDefaultItemLabelGenerator( new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelPaint(Color.YELLOW);
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition( ItemLabelAnchor.INSIDE6, TextAnchor.BOTTOM_CENTER));

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.BLUE, 0.0f, 0.0f, new Color(0, 0, 64));
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.GREEN, 0.0f, 0.0f, new Color(0, 64, 0));
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        return chart;
    }

    private static double getStandardDeviation(int[] arr) {
        double sum = 0.0;
        double standardDeviation = 0.0;
        double mean = 0.0;
        double res = 0.0;
        double sq = 0.0;


        int n = arr.length;


        for (int i = 0; i < n; i++) {
            sum = sum + arr[i];
        }

        mean = sum / (n);
        for (int i = 0; i < n; i++) {
            standardDeviation = standardDeviation + Math.pow((arr[i] - mean), 2);
        }

        sq = standardDeviation / n;
        res = Math.sqrt(sq);
        return res;
    }
}
