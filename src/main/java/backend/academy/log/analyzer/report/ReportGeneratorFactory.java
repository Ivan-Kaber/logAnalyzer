package backend.academy.log.analyzer.report;

import backend.academy.log.analyzer.exception.UnsupportedReportFormat;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportGeneratorFactory {
    public static ReportGenerator create(String reportType) {
        return switch (reportType.toLowerCase()) {
            case "adoc" -> new AsciiDocReportGenerator();
            case "markdown" -> new MarkdownReportGenerator();
            default -> throw new UnsupportedReportFormat("Unsupported report format: " + reportType);
        };
    }
}
