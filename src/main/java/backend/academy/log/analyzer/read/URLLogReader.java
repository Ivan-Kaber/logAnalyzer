package backend.academy.log.analyzer.read;

import backend.academy.log.analyzer.exception.ConnectionFailedException;
import backend.academy.log.analyzer.exception.InvalidHttpResponseCodeException;
import backend.academy.log.analyzer.exception.InvalidUrlException;
import backend.academy.log.analyzer.model.LogInformation;
import backend.academy.log.analyzer.parse.LogParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class URLLogReader implements LogReader {
    private final String pattern;
    private final HttpClient client;

    public URLLogReader(String pattern) {
        this.pattern = pattern;
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public Stream<LogInformation> readLogs() {
        HttpResponse<InputStream> response = connectToUrl();
        return parseFile(response);
    }

    @Override
    public List<String> getFileName() {
        return List.of(pattern);
    }

    private Stream<LogInformation> parseFile(HttpResponse<InputStream> response) {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
            return reader.lines()
                .map(LogParser::parseLine)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList()
                .stream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<InputStream> connectToUrl() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(pattern))
                .GET()
                .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new InvalidHttpResponseCodeException("Failed to connect to URL: " + pattern
                                                           + ", Response Code: " + response.statusCode());
            }
            return response;
        } catch (IllegalArgumentException | URISyntaxException e) {
            throw new InvalidUrlException("Invalid URL: " + pattern, e);
        } catch (InterruptedException | IOException e) {
            throw new ConnectionFailedException("error while connecting to URL: " + pattern, e);
        }
    }
}
