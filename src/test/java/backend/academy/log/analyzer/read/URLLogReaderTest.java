package backend.academy.log.analyzer.read;

import backend.academy.log.analyzer.exception.InvalidHttpResponseCodeException;
import backend.academy.log.analyzer.exception.InvalidUrlException;
import backend.academy.log.analyzer.model.LogInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class URLLogReaderTest {

    private static final String VALID_URL = "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs";
    private static final String INVALID_URL = "http://example.com/logs/invalid.log";
    private URLLogReader urlLogReader;
    private HttpClient client;

    @BeforeEach
    void setUp() {
        urlLogReader = new URLLogReader(VALID_URL);
        client = mock(HttpClient.class);
    }

    @Test
    @DisplayName("Should return logs")
    void testReadLogs_ShouldReturnLogs() throws Exception {
        HttpResponse<InputStream> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(new ByteArrayInputStream("log1\nlog2".getBytes()));

        HttpRequest mockRequest = mock(HttpRequest.class);
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofInputStream()))).thenReturn(mockResponse);

        List<LogInformation> logs = urlLogReader.readLogs().toList();
        assertEquals(51462, logs.size());
    }

    @Test
    @DisplayName("Should throw InvalidHttpResponseCodeException when response code is not 200")
    void testConnectToUrl_ShouldThrowInvalidHttpResponseCodeException_WhenUrlIsInvalid() {
        URLLogReader invalidUrlReader = new URLLogReader(INVALID_URL);

        InvalidHttpResponseCodeException exception = assertThrows(InvalidHttpResponseCodeException.class, invalidUrlReader::readLogs);
        assertEquals("Failed to connect to URL: http://example.com/logs/invalid.log, Response Code: 404", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidUrlException when mistake in Uri syntax")
    void testConnectToUrl_ShouldThrowInvalidUrlException_WhenUrlIsInvalid() {
        URLLogReader invalidUrlReader = new URLLogReader("invalid-url");

        InvalidUrlException exception = assertThrows(InvalidUrlException.class, invalidUrlReader::readLogs);
        assertEquals("Invalid URL: invalid-url", exception.getMessage());
    }


    @Test
    @DisplayName("Should return file name")
    void testGetFileName_ShouldReturnPattern() {
        List<String> fileNames = urlLogReader.getFileName();
        assertEquals(1, fileNames.size());
        assertEquals(VALID_URL, fileNames.get(0));
    }
}
