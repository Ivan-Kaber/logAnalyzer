package backend.academy.log.analyzer.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MarkdownReportGeneratorTest {
    private MarkdownReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        reportGenerator = new MarkdownReportGenerator();
    }

    @Test
    @DisplayName("Should return .md")
    void testGetSaveFormat_ShouldReturnMd() {
        assertEquals(".md", reportGenerator.getSaveFormat(), "The save format should be .md");
    }

    @Test
    @DisplayName("Should append main header")
    void testAppendMainHeader_ShouldAppendHeaderCorrectly() {
        StringBuilder stringBuilder = new StringBuilder();
        reportGenerator.appendMainHeader(stringBuilder, "Main Header");

        String expected = "## Main Header\n";
        assertEquals(expected, stringBuilder.toString(), "Main header should be formatted correctly");
    }

    @Test
    @DisplayName("Should append sub header")
    void testAppendHeader_ShouldAppendSubHeaderCorrectly() {
        StringBuilder stringBuilder = new StringBuilder();
        reportGenerator.appendHeader(stringBuilder, "Sub Header");

        String expected = "#### Sub Header\n\n";
        assertEquals(expected, stringBuilder.toString(), "Sub header should be formatted correctly");
    }

    @Test
    @DisplayName("Should append table header")
    void testAppendTableHeader_ShouldAppendTableHeaderCorrectly() {
        StringBuilder stringBuilder = new StringBuilder();
        reportGenerator.appendTableHeader(stringBuilder, "Column 1", "Column 2", "Column 3");

        String expected = "| Column 1 | Column 2 | Column 3 \n| --- | --- | --- | \n";
        assertEquals(expected, stringBuilder.toString(), "Table header should be formatted correctly");
    }

}
