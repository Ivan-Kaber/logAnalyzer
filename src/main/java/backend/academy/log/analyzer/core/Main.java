package backend.academy.log.analyzer.core;

import backend.academy.log.analyzer.read.LogReader;
import backend.academy.log.analyzer.read.LogReaderFactory;
import backend.academy.log.analyzer.report.ReportGenerator;
import backend.academy.log.analyzer.report.ReportGeneratorFactory;
import backend.academy.log.analyzer.statistic.LogFilter;
import backend.academy.log.analyzer.statistic.StatisticsCalculator;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("RegexpSinglelineJava")
public class Main {
    private static final String DEFAULT_SAVE_PATH = "./src/main/resources/statistic/report";

    public static void main(String[] args) {
        ArgumentParser argumentParser = new ArgumentParser();
        argumentParser.initialize(args);

        LogReader logReader = LogReaderFactory.create(argumentParser.path());
        StatisticsCalculator statisticsCalculator = calculateStatistic(logReader, argumentParser);

        generateReport(statisticsCalculator, argumentParser);
    }

    private static StatisticsCalculator calculateStatistic(LogReader logReader, ArgumentParser argumentParser) {
        StatisticsCalculator statisticsCalculator = new StatisticsCalculator();

        LogFilter logFilter = new LogFilter(
            argumentParser.fromDate(),
            argumentParser.toDate(),
            statisticsCalculator,
            argumentParser.filterField(),
            argumentParser.filterValue());
        logFilter.check(logReader.readLogs());

        statisticsCalculator.setArgsInformation(
            logReader.getFileName(),
            argumentParser.fromDate(),
            argumentParser.toDate());
        return statisticsCalculator;
    }

    private static void generateReport(StatisticsCalculator statisticsCalculator, ArgumentParser argumentParser) {
        ReportGenerator reportGenerator = ReportGeneratorFactory.create(argumentParser.format());
        String savePath = DEFAULT_SAVE_PATH + reportGenerator.getSaveFormat();
        reportGenerator.generateReport(statisticsCalculator, savePath);
        System.out.println("Report generated successfully at: " + savePath);
    }
}
