package backend.academy.log.analyzer.read;

import backend.academy.log.analyzer.exception.FileSearchException;
import backend.academy.log.analyzer.exception.ReadLogException;
import backend.academy.log.analyzer.model.LogInformation;
import backend.academy.log.analyzer.parse.LogParser;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FileLogReader implements LogReader {
    private List<Path> filteredFiles = new ArrayList<>();
    private final String pattern;
    private static final Path START_PATH = Paths.get("./src/main/resources/");

    public FileLogReader(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public Stream<LogInformation> readLogs() {
        findFilteredFiles();

        if (filteredFiles.isEmpty()) {
            throw new FileSearchException("No files found");
        }

        return filteredFiles.stream()
            .flatMap(this::parseFile);
    }

    @Override
    public List<String> getFileName() {
        return filteredFiles.stream()
            .map(Path::getFileName)
            .map(Path::toString)
            .toList();
    }

    private void findFilteredFiles() {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**" + pattern);
        try (Stream<Path> paths = Files.walk(START_PATH)) {
            filteredFiles = paths
                .filter(Files::isRegularFile)
                .filter(pathMatcher::matches)
                .toList();
        } catch (IOException e) {
            throw new FileSearchException("Failed to search for files", e);
        }
    }

    private Stream<LogInformation> parseFile(Path path) {
        try {
            return Files.lines(path)
                .map(LogParser::parseLine)
                .filter(Optional::isPresent)
                .map(Optional::get);
        } catch (IOException e) {
            throw new ReadLogException("Failed to read log file", e);
        }
    }
}
