package backend.academy.log.analyzer.report;

@SuppressWarnings("MultipleStringLiterals")
public class AsciiDocReportGenerator extends AbstractReportGenerator {
    private static final String SAVE_FORMAT = ".adoc";

    @Override
    public String getSaveFormat() {
        return SAVE_FORMAT;
    }

    @Override
    void appendMainHeader(StringBuilder stringBuilder, String title) {
        stringBuilder.append("== ")
            .append(title)
            .append('\n')
            .append("|===\n");
    }

    @Override
    void appendHeader(StringBuilder stringBuilder, String title) {
        stringBuilder.append("|===\n")
            .append("==== ")
            .append(title)
            .append("\n\n")
            .append("|===\n");
    }

    @Override
    void appendTableHeader(StringBuilder stringBuilder, String... columns) {
        stringBuilder.append(formatRow(columns))
            .append('\n');
    }
}
