package backend.academy.log.analyzer.report;

import backend.academy.log.analyzer.exception.GenerateReportException;
import backend.academy.log.analyzer.statistic.StatisticsCalculator;
import backend.academy.log.analyzer.util.HttpStatusCode;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

@SuppressWarnings("MultipleStringLiterals")
public abstract class AbstractReportGenerator implements ReportGenerator {
    @Override
    public void generateReport(StatisticsCalculator statisticsCalculator, String outputFile) {
        try (FileWriter writer = new FileWriter(outputFile, Charset.defaultCharset(), false)) {
            StringBuilder stringBuilder = new StringBuilder();

            appendGeneralInfo(statisticsCalculator, stringBuilder);
            appendResourceBlock(stringBuilder, statisticsCalculator);
            appendStatusCodeBlock(stringBuilder, statisticsCalculator);
            appendRequestTypeBlock(stringBuilder, statisticsCalculator);

            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new GenerateReportException("Failed to generate report", e);
        }
    }

    String formatRow(String... columns) {
        return "| " + String.join(" | ", columns) + " \n";
    }

    private void appendGeneralInfo(StatisticsCalculator statisticsCalculator, StringBuilder stringBuilder) {
        appendMainHeader(stringBuilder, "Общая информация");
        appendTableHeader(stringBuilder, "Метрика", "Значение");

        stringBuilder.append(formatRow("Файл(-ы)", statisticsCalculator.fileNames().stream()
                .map(x -> "`" + x + "`")
                .collect(Collectors.joining(", "))))
            .append(formatRow("Начальная дата", statisticsCalculator.fromDate()))
            .append(formatRow("Конечная дата", statisticsCalculator.toDate()))
            .append(formatRow("Количество запросов", String.valueOf(statisticsCalculator.countRequests())))
            .append(formatRow("Средний размер ответа", statisticsCalculator.getAverageResponse() + "b"))
            .append(formatRow("95p размера ответа", statisticsCalculator.get95thPercentile() + "b"))
            .append(formatRow("Количество уникальных адресов",
                String.valueOf(statisticsCalculator.ipAddressCount().size())))
            .append('\n');
    }

    private void appendResourceBlock(StringBuilder stringBuilder, StatisticsCalculator statisticsCalculator) {
        appendHeader(stringBuilder, "Запрашиваемые ресурсы");
        appendTableHeader(stringBuilder, "Ресурс", "Количество");

        statisticsCalculator.getSortedHashMap(statisticsCalculator.resourceCount())
            .forEach((resource, count) -> stringBuilder.append(formatRow("`" + resource + "`", count.toString())));
        stringBuilder.append('\n');
    }

    private void appendStatusCodeBlock(StringBuilder stringBuilder, StatisticsCalculator statisticsCalculator) {
        appendHeader(stringBuilder, "Коды ответа");
        appendTableHeader(stringBuilder, "Код", "Имя", "Количество");

        statisticsCalculator.getSortedHashMap(statisticsCalculator.statusCodeCount())
            .forEach((statusCode, count) -> stringBuilder.append(formatRow(statusCode.toString(),
                HttpStatusCode.getStatusName(statusCode),
                count.toString())));
        stringBuilder.append('\n');
    }

    private void appendRequestTypeBlock(StringBuilder stringBuilder, StatisticsCalculator statisticsCalculator) {
        appendHeader(stringBuilder, "Типы запросов");
        appendTableHeader(stringBuilder, "Запрос", "Количество");

        statisticsCalculator.getSortedHashMap(statisticsCalculator.requestTypeCount())
            .forEach((requestType, count) ->
                stringBuilder.append(formatRow("`" + requestType + "`", count.toString())));
        stringBuilder.append('\n');
    }

    abstract void appendMainHeader(StringBuilder stringBuilder, String title);

    abstract void appendHeader(StringBuilder stringBuilder, String title);

    abstract void appendTableHeader(StringBuilder stringBuilder, String... columns);
}
