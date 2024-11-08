package backend.academy.log.analyzer.exception;

public class UnknownFilterException extends RuntimeException {
    public UnknownFilterException(String message) {
        super(message);
    }
}
