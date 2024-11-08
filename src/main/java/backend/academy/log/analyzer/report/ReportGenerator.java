package backend.academy.log.analyzer.report;

import backend.academy.log.analyzer.statistic.StatisticsCalculator;

public interface ReportGenerator {
    void generateReport(StatisticsCalculator statisticsCalculator, String outputFile);

    String getSaveFormat();
}
