package backend.academy.log.analyzer.read;

import backend.academy.log.analyzer.model.LogInformation;
import java.util.List;
import java.util.stream.Stream;

public interface LogReader {
    Stream<LogInformation> readLogs();

    List<String> getFileName();
}
