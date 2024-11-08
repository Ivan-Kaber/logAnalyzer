package backend.academy.log.analyzer.model;

import java.time.LocalDateTime;

public record LogInformation(
    String remoteAddress,
    String remoteUser,
    LocalDateTime time,
    String request,
    Integer status,
    Integer bodyBytesSent,
    String httpReferer,
    String httpUserAgent) {
}
