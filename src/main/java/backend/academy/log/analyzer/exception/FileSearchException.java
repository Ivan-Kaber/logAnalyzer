package backend.academy.log.analyzer.exception;

public class FileSearchException extends RuntimeException {
    public FileSearchException(String name) {
        super(name);
    }

    public FileSearchException(String name, Throwable cause) {
        super(name, cause);
    }
}
