package backend.academy.log.analyzer.core;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArgumentParser {
    @Parameter(names = "--path", description = "Path to log file", required = true)
    private String path;
    @Parameter(names = "--from", description = "Start date")
    private String from;
    @Parameter(names = "--to", description = "End date")
    private String to;
    @Parameter(names = "--format", description = "Output format")
    private String format = "markdown";
    @Parameter(names = "--filter-field", description = "Field to filter by")
    private String filterField;
    @Parameter(names = "--filter-value", description = "Value to filter by")
    private String filterValue;

    private LocalDate fromDate;
    private LocalDate toDate;

    public ArgumentParser(String[] args) {
        JCommander.newBuilder()
            .addObject(this)
            .build()
            .parse(args);
    }

}
