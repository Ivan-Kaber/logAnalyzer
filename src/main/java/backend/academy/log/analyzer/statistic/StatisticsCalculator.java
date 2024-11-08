package backend.academy.log.analyzer.statistic;

import backend.academy.log.analyzer.model.LogInformation;
import com.google.common.math.Quantiles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatisticsCalculator {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Pattern RESOURCE_PATTERN = Pattern.compile("^[A-Z]+\\s+(/[^\\s]*)");
    private static final int PERCENTILE = 95;
    private List<String> fileNames = new ArrayList<>();
    private String fromDate;
    private String toDate;
    private int countRequests = 0;
    private List<Integer> response = new ArrayList<>();
    private Map<String, Integer> resourceCount = new HashMap<>();
    private Map<Integer, Integer> statusCodeCount = new HashMap<>();
    private Map<String, Integer> requestTypeCount = new HashMap<>();
    private Set<String> ipAddressCount = new HashSet<>();

    public void collect(LogInformation logInformation) {
        countRequests++;
        response.add(logInformation.bodyBytesSent());
        resourceCount.merge(getResourceName(logInformation.request()), 1, Integer::sum);
        statusCodeCount.merge(logInformation.status(), 1, Integer::sum);
        requestTypeCount.merge(getRequestType(logInformation.request()), 1, Integer::sum);
        ipAddressCount.add(logInformation.remoteAddress());
    }

    public void setArgsInformation(List<String> fileNames, LocalDate fromDate, LocalDate toDate) {
        this.fileNames = fileNames;
        this.fromDate = dateHandling(fromDate);
        this.toDate = dateHandling(toDate);
    }

    public long getAverageResponse() {
        if (!response.isEmpty()) {
            return response.stream()
                       .mapToLong(Integer::longValue)
                       .sum() / response.size();
        }
        return 0;
    }

    public int get95thPercentile() {
        if (!response.isEmpty()) {
            return (int) Quantiles.percentiles().index(PERCENTILE).compute(response);
        }
        return 0;
    }

    public <K, V extends Comparable<V>> HashMap<K, V> getSortedHashMap(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
            new LinkedList<>(map.entrySet());

        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        HashMap<K, V> temp = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private String dateHandling(LocalDate date) {
        return date == null ? "-" : date.format(DATE_TIME_FORMATTER);
    }

    private String getRequestType(String request) {
        return request.split(" ")[0];
    }

    private String getResourceName(String request) {
        Matcher matcher = RESOURCE_PATTERN.matcher(request);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
