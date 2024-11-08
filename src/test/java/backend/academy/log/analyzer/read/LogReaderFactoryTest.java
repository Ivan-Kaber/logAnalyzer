package backend.academy.log.analyzer.read;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LogReaderFactoryTest {

    @Test
    @DisplayName("Should create FileLogReader when pattern is *.log")
    void create_FileLogReader() {
        LogReader logReader = LogReaderFactory.create("*.log");
        assertInstanceOf(FileLogReader.class, logReader);
    }

    @Test
    @DisplayName("Should create URLLogReader when pattern is https://example.com/log.txt")
    void create_URLLogReader() {
        LogReader logReader = LogReaderFactory.create("https://example.com/log.txt");
        assertInstanceOf(URLLogReader.class, logReader);
    }

    @Test
    @DisplayName("Should create FileLogReader when pattern is htp://example.com/logs/access.log")
    void create_UnknownLogReader() {
        LogReader logReader = LogReaderFactory.create("htp://example.com/logs/access.log");
        assertInstanceOf(FileLogReader.class, logReader);
    }
}
