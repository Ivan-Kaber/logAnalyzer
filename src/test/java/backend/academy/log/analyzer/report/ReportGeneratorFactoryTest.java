package backend.academy.log.analyzer.report;

import backend.academy.log.analyzer.exception.UnsupportedReportFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportGeneratorFactoryTest {

    @Test
    @DisplayName("Should return AsciiDocReportGenerator when report type is 'adoc'")
    void testCreate_ShouldReturnAsciiDocReportGenerator() {
        ReportGenerator reportGenerator = ReportGeneratorFactory.create("adoc");
        assertTrue(reportGenerator instanceof AsciiDocReportGenerator, "Should return AsciiDocReportGenerator");
    }

    @Test
    @DisplayName("Should return MarkdownReportGenerator when report type is 'markdown'")
    void testCreate_ShouldReturnMarkdownReportGenerator() {
        ReportGenerator reportGenerator = ReportGeneratorFactory.create("markdown");
        assertTrue(reportGenerator instanceof MarkdownReportGenerator, "Should return MarkdownReportGenerator");
    }

    @Test
    @DisplayName("Should throw UnsupportedReportFormat when report type is unsupported")
    void testCreate_ShouldThrowUnsupportedReportFormat() {
        String unsupportedFormat = "pdf";

        UnsupportedReportFormat exception = assertThrows(UnsupportedReportFormat.class, () -> {
            ReportGeneratorFactory.create(unsupportedFormat);
        });

        assertEquals("Unsupported report format: " + unsupportedFormat, exception.getMessage());
    }

    @Test
    @DisplayName("Should handle case insensitive report type")
    void testCreate_ShouldHandleCaseInsensitiveReportType() {
        ReportGenerator reportGenerator1 = ReportGeneratorFactory.create("ADOC");
        assertTrue(reportGenerator1 instanceof AsciiDocReportGenerator, "Should return AsciiDocReportGenerator");

        ReportGenerator reportGenerator2 = ReportGeneratorFactory.create("MARKDOWN");
        assertTrue(reportGenerator2 instanceof MarkdownReportGenerator, "Should return MarkdownReportGenerator");
    }
}
