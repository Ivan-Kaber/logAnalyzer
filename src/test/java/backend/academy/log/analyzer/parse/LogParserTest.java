package backend.academy.log.analyzer.parse;

import backend.academy.log.analyzer.model.LogInformation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

        LogInformation logInformation = LogParser.parseLine(logLine);

        assertNotNull(logInformation);
        assertEquals("74.125.60.158", logInformation.remoteAddress());
        assertEquals("-", logInformation.remoteUser());
        assertEquals(LocalDateTime.parse("18/May/2015:13:05:52 +0000", DATE_TIME_FORMATTER), logInformation.time());
        assertEquals("GET /downloads/product_1 HTTP/1.1", logInformation.request());
        assertEquals(404, logInformation.status());
        assertEquals(345, logInformation.bodyBytesSent());
        assertEquals("Debian APT-HTTP/1.3 (1.0.1ubuntu2)", logInformation.httpUserAgent());
    }

    @Test
    @DisplayName("Should return null when string has invalid date format")
    void testParseLine_InvalidDateFormat() {
        String logLine = "74.125.60.158 - " +
                         "- " +
                         "[2022-May-18:13:05:52 +0000] " +
                         "\"GET /downloads/product_1 HTTP/1.1\" " +
                         "404 " +
                         "345 " +
                         "\"-\" " +
                         "\"Debian APT-HTTP/1.3 (1.0.1ubuntu2)\"";
        LogInformation logInformation = LogParser.parseLine(logLine);

        assertNull(logInformation);
    }

    @Test
    @DisplayName("Should return Empty LogInformation when string has invalid pattern")
    void testParseLine_InvalidLogEntry() {
        String logEntry = "Invalid log entry";

        LogInformation logInformation = LogParser.parseLine(logEntry);

        assertNull(logInformation);
    }

    @Test
    @DisplayName("Should return Empty LogInformation when string has missing fields")
    void testParseLine_MissingFields() {
        String logEntry = "192.168.0.1 - user1 [10/Oct/2023:13:55:36 +0000] \"GET /index.html HTTP/1.1\" 200";

        LogInformation logInformation = LogParser.parseLine(logEntry);

        assertNull(logInformation);
    }

    @Test
    @DisplayName("Should return Empty LogInformation when string has invalid type of not numeric status code")
    void testParseLine_NonNumericStatusCode() {
        String logEntry = "192.168.0.1 - user1 [10/Oct/2023:13:55:36 +0000] " +
                          "\"GET /index.html HTTP/1.1\" ABC 1234 " +
                          "\"http://example.com\" " +
                          "\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36\"";

        LogInformation logInformation = LogParser.parseLine(logEntry);

        assertNull(logInformation);
    }

}
