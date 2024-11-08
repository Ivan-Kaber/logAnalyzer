package backend.academy.log.analyzer.report;

public class MarkdownReportGenerator extends AbstractReportGenerator {
    private static final String SAVE_FORMAT = ".md";

    @Override
    public String getSaveFormat() {
        return SAVE_FORMAT;
    }

    @Override
    void appendMainHeader(StringBuilder stringBuilder, String title) {
        stringBuilder.append("## ")
            .append(title)
            .append('\n');
    }

    @Override
    void appendHeader(StringBuilder stringBuilder, String title) {
        stringBuilder.append("#### ")
            .append(title)
            .append("\n\n");
    }

    @Override
    void appendTableHeader(StringBuilder stringBuilder, String... columns) {
        stringBuilder.append(formatRow(columns))
            .append("| ")
            .append("--- | ".repeat(columns.length))
            .append('\n');
    }
}
