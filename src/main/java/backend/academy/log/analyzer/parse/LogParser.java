package backend.academy.log.analyzer.parse;

import backend.academy.log.analyzer.model.LogInformation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LogParser {
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(?<remoteAddr>\\S+) - "
        + "(?<remoteUser>\\S+) "
        + "\\[(?<timeLocal>[^]]+)]"
        + " \"(?<request>[^\"]+)\""
        + " (?<status>\\d{3})"
        + " (?<bodyBytesSent>\\d+) "
        + "\"(?<httpReferer>[^\"]*)\""
        + " \"(?<httpUserAgent>[^\"]*)\"$");

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.US);

    public static Optional<LogInformation> parseLine(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.matches()) {
            try {
                String remoteAddress = matcher.group("remoteAddr");
                String remoteUser = matcher.group("remoteUser");
                LocalDateTime time = LocalDateTime.parse(matcher.group("timeLocal"), DATE_TIME_FORMATTER);
                String request = matcher.group("request");
                Integer status = Integer.valueOf(matcher.group("status"));
                Integer bodyBytesSent = Integer.valueOf(matcher.group("bodyBytesSent"));
                String httpReferer = matcher.group("httpReferer");
                String httpUserAgent = matcher.group("httpUserAgent");
                return Optional.of(
                    new LogInformation(remoteAddress, remoteUser, time, request, status, bodyBytesSent, httpReferer,
                        httpUserAgent));
            } catch (RuntimeException e) {
                System.err.println("Failed to parse line: " + line + " due to " + e.getMessage());
                return Optional.empty();
            }
        }
        System.err.println("Failed to parse: " + line + " pattern not matched");
        return Optional.empty();
    }
}
