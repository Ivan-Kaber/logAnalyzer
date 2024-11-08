package backend.academy.log.analyzer.statistic;

import backend.academy.log.analyzer.exception.UnknownFilterException;
import backend.academy.log.analyzer.model.LogInformation;
import java.time.LocalDate;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LogFilter {
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final StatisticsCalculator statistics;
    private final String filterField;
    private final String filterValue;

    public void check(Stream<LogInformation> logInformationStream) {
        logInformationStream.filter(this::filterByDate)
            .filter(this::filterByField)
            .forEach(statistics::collect);
    }

    private boolean filterByDate(LogInformation logInformation) {
        return (fromDate == null || logInformation.time().toLocalDate().isAfter(fromDate))
               && (toDate == null || logInformation.time().toLocalDate().isBefore(toDate));
    }

    boolean filterByField(LogInformation logInformation) {
        if (filterField == null || filterValue == null) {
            return true;
        }

        return switch (filterField) {
            case "address" -> logInformation
                .remoteAddress()
                .equalsIgnoreCase(filterValue);
            case "method" -> logInformation
                .request()
                .split(" ")[0]
                .equalsIgnoreCase(filterValue);
            case "status" -> logInformation
                .status()
                .toString()
                .equalsIgnoreCase(filterValue);
            case "agent" -> logInformation
                .httpUserAgent()
                .contains(filterValue);
            default -> throw new UnknownFilterException("Unknown filter field: " + filterField);
        };
    }
}
