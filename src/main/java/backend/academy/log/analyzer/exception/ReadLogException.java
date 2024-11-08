package backend.academy.log.analyzer.exception;

public class ReadLogException extends RuntimeException {
    public ReadLogException(String message, Throwable cause) {
        super(message, cause);
    }
}
