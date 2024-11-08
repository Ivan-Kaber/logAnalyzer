package backend.academy.log.analyzer.read;

import backend.academy.log.analyzer.exception.FileSearchException;
import backend.academy.log.analyzer.model.LogInformation;
import backend.academy.log.analyzer.parse.LogParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class FileLogReaderTest {
    private final Path START_PATH = Path.of("./src/main/resources/");
    private FileLogReader logReader;

    @BeforeEach
    void setUp() {
        logReader = new FileLogReader("*.log");
    }

    @Test
    @DisplayName("Should read logs successfully")
    void testReadLogs_Success() {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            Path mockPath = Paths.get("./src/main/resources/sample.log");
            List<Path> mockPaths = List.of(mockPath);
            mockedFiles.when(() -> Files.walk(START_PATH)).thenReturn(mockPaths.stream());
            mockedFiles.when(() -> Files.isRegularFile(any(Path.class))).thenReturn(true);
            mockedFiles.when(() -> Files.lines(any(Path.class))).thenReturn(Stream.of(
                "192.168.0.1 - user1 [10/Oct/2023:13:55:36 +0000] \"GET /index.html HTTP/1.1\" 200 1234 \"http://example.com\" \"Mozilla/5.0\""
            ));

            try (MockedStatic<LogParser> mockedParser = mockStatic(LogParser.class)) {
                LogInformation logInfo =
                    new LogInformation("192.168.0.1", "user1", null, "GET /index.html HTTP/1.1", 200, 1234,
                        "http://example.com", "Mozilla/5.0");
                mockedParser.when(() -> LogParser.parseLine(any(String.class))).thenReturn(logInfo);

                Stream<LogInformation> logs = logReader.readLogs();
                assertNotNull(logs);
                assertEquals(1, logs.count());
            }
        }
    }

    @Test
    @DisplayName("Should throw FileSearchException when file search fails")
    void testReadLogs_FileSearchException() {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.walk(START_PATH)).thenThrow(IOException.class);

            FileSearchException exception = assertThrows(FileSearchException.class, () -> logReader.readLogs());
            assertEquals("Failed to search for files", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Should throw FileSearchException when no files found")
    void testReadLogs_NoFilesFound() {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.walk(START_PATH)).thenReturn(Stream.empty());

            FileSearchException exception = assertThrows(FileSearchException.class, () -> logReader.readLogs());
            assertEquals("No files found", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Should get file names successfully")
    void testGetFileName_Success() {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            Path mockPath1 = Paths.get("./src/main/resources/sample1.log");
            Path mockPath2 = Paths.get("./src/main/resources/sample2.log");
            List<Path> mockPaths = List.of(mockPath1, mockPath2);
            mockedFiles.when(() -> Files.walk(START_PATH)).thenReturn(mockPaths.stream());
            mockedFiles.when(() -> Files.isRegularFile(any(Path.class))).thenReturn(true);

            logReader.readLogs();
            List<String> fileNames = logReader.getFileName();

            assertEquals(2, fileNames.size());
            assertTrue(fileNames.contains("sample1.log"));
            assertTrue(fileNames.contains("sample2.log"));
        }
    }

    @Test
    @DisplayName("Should throw FileSearchException when no files match the pattern")
    void testGetFileName_NoFilesFound() {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.walk(START_PATH)).thenReturn(Stream.empty());

            assertThrows(FileSearchException.class, () -> {
                logReader.readLogs();
            });
        }
    }

    @Test
    @DisplayName("Should throw FileSearchException when the directory is empty")
    void testGetFileName_EmptyDirectory() {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.walk(START_PATH)).thenReturn(Stream.empty());

            assertThrows(FileSearchException.class, () -> {
                logReader.readLogs();
                logReader.getFileName();
            });
        }
    }

    @Test
    @DisplayName("Should get only one file name when only one file matches the pattern")
    void testGetFileName_WithDifferentPattern() {
        logReader = new FileLogReader("sample1.log"); // изменяем паттерн

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            Path mockPath1 = Paths.get("./src/main/resources/sample1.log");
            Path mockPath2 = Paths.get("./src/main/resources/sample2.log");
            List<Path> mockPaths = List.of(mockPath1, mockPath2);
            mockedFiles.when(() -> Files.walk(START_PATH)).thenReturn(mockPaths.stream());
            mockedFiles.when(() -> Files.isRegularFile(any(Path.class))).thenReturn(true);

            logReader.readLogs();
            List<String> fileNames = logReader.getFileName();

            assertEquals(1, fileNames.size());
            assertTrue(fileNames.contains("sample1.log"));
            assertFalse(fileNames.contains("sample2.log"));
        }
    }
}
