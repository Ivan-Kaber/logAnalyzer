package backend.academy.log.analyzer.read;

import java.net.URI;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LogReaderFactory {
    public LogReader create(String path) {
        try {
            URI uri = new URI(path);
            uri.toURL();
            return new URLLogReader(path);
        } catch (Exception e) {
            return new FileLogReader(path);
        }
    }
}
