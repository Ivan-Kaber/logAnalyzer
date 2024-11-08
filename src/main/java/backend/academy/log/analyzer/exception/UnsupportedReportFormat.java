package backend.academy.log.analyzer.exception;

public class UnsupportedReportFormat extends RuntimeException {
    public UnsupportedReportFormat(String message) {
        super(message);
    }
}
