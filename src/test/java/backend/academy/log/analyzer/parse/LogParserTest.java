package backend.academy.log.analyzer.parse;

import backend.academy.log.analyzer.model.LogInformation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogParserTest {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.US);

    @Test
    @DisplayName("Parse valid log entry")
    void testParseLine_ValidLogEntry() {
        String logLine = "74.125.60.158 - " +
                         "- " +
                         "[18/May/2015:13:05:52 +0000] " +
                         "\"GET /downloads/product_1 HTTP/1.1\" " +
                         "404 " +
                         "345 " +
                         "\"-\" " +
                         "\"Debian APT-HTTP/1.3 (1.0.1ubuntu2)\"";

        Optional<LogInformation> logInformation = LogParser.parseLine(logLine);

        assertTrue(logInformation.isPresent());

        LogInformation info = logInformation.orElseThrow(() -> new AssertionError("Expected a valid log entry"));
        assertEquals("74.125.60.158", info.remoteAddress());
        assertEquals("-", info.remoteUser());
        assertEquals(LocalDateTime.parse("18/May/2015:13:05:52 +0000", DATE_TIME_FORMATTER), info.time());
        assertEquals("GET /downloads/product_1 HTTP/1.1", info.request());
        assertEquals(404, info.status());
        assertEquals(345, info.bodyBytesSent());
        assertEquals("Debian APT-HTTP/1.3 (1.0.1ubuntu2)", info.httpUserAgent());
    }

    @Test
    @DisplayName("Should return empty Optional for invalid date format")
    void testParseLine_InvalidDateFormat() {
        String logLine = "74.125.60.158 - " +
                         "- " +
                         "[2022-May-18:13:05:52 +0000] " +
                         "\"GET /downloads/product_1 HTTP/1.1\" " +
                         "404 " +
                         "345 " +
                         "\"-\" " +
                         "\"Debian APT-HTTP/1.3 (1.0.1ubuntu2)\"";

        Optional<LogInformation> logInformation = LogParser.parseLine(logLine);

        assertFalse(logInformation.isPresent());
    }

    @Test
    @DisplayName("Should return empty Optional for invalid log entry pattern")
    void testParseLine_InvalidLogEntry() {
        String logEntry = "Invalid log entry";

        Optional<LogInformation> logInformation = LogParser.parseLine(logEntry);

        assertFalse(logInformation.isPresent());
    }

    @Test
    @DisplayName("Should return empty Optional for log entry with missing fields")
    void testParseLine_MissingFields() {
        String logEntry = "192.168.0.1 - user1 [10/Oct/2023:13:55:36 +0000] \"GET /index.html HTTP/1.1\" 200";

        Optional<LogInformation> logInformation = LogParser.parseLine(logEntry);

        assertFalse(logInformation.isPresent());
    }

    @Test
    @DisplayName("Should return empty Optional for non-numeric status code")
    void testParseLine_NonNumericStatusCode() {
        String logEntry = "192.168.0.1 - user1 [10/Oct/2023:13:55:36 +0000] " +
                          "\"GET /index.html HTTP/1.1\" ABC 1234 " +
                          "\"http://example.com\" " +
                          "\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36\"";

        Optional<LogInformation> logInformation = LogParser.parseLine(logEntry);

        assertFalse(logInformation.isPresent());
    }
}
