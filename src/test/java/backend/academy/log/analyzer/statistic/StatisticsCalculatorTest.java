package backend.academy.log.analyzer.statistic;

import backend.academy.log.analyzer.model.LogInformation;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatisticsCalculatorTest {
    private StatisticsCalculator statisticsCalculator;
    private LogInformation mockLog;

    @BeforeEach
    void setUp() {
        statisticsCalculator = new StatisticsCalculator();
        mockLog = mock(LogInformation.class);
    }

    @Test
    @DisplayName("Collect statistics")
    void testCollect() {
        when(mockLog.bodyBytesSent()).thenReturn(500);
        when(mockLog.request()).thenReturn("GET /index.html HTTP/1.1");
        when(mockLog.status()).thenReturn(200);
        when(mockLog.remoteAddress()).thenReturn("192.168.0.1");

        statisticsCalculator.collect(mockLog);

        assertEquals(1, statisticsCalculator.countRequests());
        assertEquals(1, statisticsCalculator.response().size());
        assertEquals(500, statisticsCalculator.response().get(0));
        assertEquals(1, statisticsCalculator.resourceCount().get("/index.html"));
        assertEquals(1, statisticsCalculator.statusCodeCount().get(200));
        assertEquals(1, statisticsCalculator.requestTypeCount().get("GET"));
        assertTrue(statisticsCalculator.ipAddressCount().contains("192.168.0.1"));
    }

    @Test
    @DisplayName("Get average response")
    void testGetAverageResponse() {
        statisticsCalculator.response().addAll(Arrays.asList(100, 200, 300));

        assertEquals(200, statisticsCalculator.getAverageResponse());
    }

    @Test
    @DisplayName("Get 95th percentile")
    void testGet95thPercentile() {
        statisticsCalculator.response().addAll(Arrays.asList(100, 200, 300, 400, 500));

        assertEquals(480, statisticsCalculator.get95thPercentile());
    }

    @Test
    @DisplayName("Should set args information")
    void testSetArgsInformation() {
        List<String> fileNames = Arrays.asList("file1.log", "file2.log");
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);

        statisticsCalculator.setArgsInformation(fileNames, fromDate, toDate);

        assertEquals(fileNames, statisticsCalculator.fileNames());
        assertEquals("01.01.2024", statisticsCalculator.fromDate());
        assertEquals("31.12.2024", statisticsCalculator.toDate());
    }

    @Test
    @DisplayName("Should get sorted hash map")
    void testGetSortedHashMap() {
        Map<String, Integer> unsortedMap = Map.of("Resource1", 5, "Resource2", 10, "Resource3", 3);
        Map<String, Integer> sortedMap = statisticsCalculator.getSortedHashMap(unsortedMap);

        assertEquals(Map.of("Resource2", 10, "Resource1", 5, "Resource3", 3), sortedMap);
    }
}
