package backend.academy.log.analyzer.exception;

public class InvalidHttpResponseCodeException extends RuntimeException {
    public InvalidHttpResponseCodeException(String message) {
        super(message);
    }
}
