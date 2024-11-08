package backend.academy.log.analyzer.statistic;

import backend.academy.log.analyzer.exception.UnknownFilterException;
import backend.academy.log.analyzer.model.LogInformation;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LogFilterTest {
    private LogFilter logFilter;
    private StatisticsCalculator mockStatistics;
    private LogInformation mockLogInformation;

    @BeforeEach
    void setUp() {
        mockStatistics = mock(StatisticsCalculator.class);
        mockLogInformation = mock(LogInformation.class);

        when(mockLogInformation.time()).thenReturn(LocalDateTime.of(2024, 11, 1, 12, 0));
        when(mockLogInformation.remoteAddress()).thenReturn("192.168.0.1");
        when(mockLogInformation.request()).thenReturn("GET /index.html HTTP/1.1");
        when(mockLogInformation.status()).thenReturn(200);
        when(mockLogInformation.httpUserAgent()).thenReturn("Mozilla/5.0");
    }

    @Test
    @DisplayName("Should filter by address")
    void testFilterByField_AddressMatches() {
        logFilter = new LogFilter(null, null, mockStatistics, "address", "192.168.0.1");

        assertTrue(logFilter.filterByField(mockLogInformation), "The filter should match the address.");
    }

    @Test
    @DisplayName("Should not filter by address")
    void testFilterByField_AddressDoesNotMatch() {
        logFilter = new LogFilter(null, null, mockStatistics, "address", "10.0.0.1");

        assertFalse(logFilter.filterByField(mockLogInformation), "The filter should not match a different address.");
    }

    @Test
    @DisplayName("Should filter by method")
    void testFilterByField_MethodMatches() {
        logFilter = new LogFilter(null, null, mockStatistics, "method", "GET");

        assertTrue(logFilter.filterByField(mockLogInformation), "The filter should match the request method.");
    }

    @Test
    @DisplayName("Should filter by method in lower case")
    void testFilterByField_MethodMatchesToLowerCase() {
        logFilter = new LogFilter(null, null, mockStatistics, "method", "get");

        assertTrue(logFilter.filterByField(mockLogInformation), "The filter should match the request method.");
    }

    @Test
    @DisplayName("Should not filter by method")
    void testFilterByField_MethodDoesNotMatch() {
        // Проверка, что фильтрация не пройдет, если метод не совпадает
        logFilter = new LogFilter(null, null, mockStatistics, "method", "POST");

        assertFalse(logFilter.filterByField(mockLogInformation),
            "The filter should not match a different request method.");
    }

    @Test
    @DisplayName("Should filter by status")
    void testFilterByField_StatusMatches() {
        logFilter = new LogFilter(null, null, mockStatistics, "status", "200");

        assertTrue(logFilter.filterByField(mockLogInformation), "The filter should match the status code.");
    }

    @Test
    @DisplayName("Should not filter by status")
    void testFilterByField_StatusDoesNotMatch() {
        logFilter = new LogFilter(null, null, mockStatistics, "status", "404");

        assertFalse(logFilter.filterByField(mockLogInformation),
            "The filter should not match a different status code.");
    }

    @Test
    @DisplayName("Should filter by user agent")
    void testFilterByField_AgentMatches() {
        logFilter = new LogFilter(null, null, mockStatistics, "agent", "Mozilla");

        assertTrue(logFilter.filterByField(mockLogInformation), "The filter should match the user agent.");
    }

    @Test
    @DisplayName("Should not filter by user agent")
    void testFilterByField_AgentDoesNotMatch() {
        logFilter = new LogFilter(null, null, mockStatistics, "agent", "Chrome");

        assertFalse(logFilter.filterByField(mockLogInformation), "The filter should not match a different user agent.");
    }

    @Test
    @DisplayName("Should pass with null filterField and filterValue")
    void testFilterByField_NullFilterFieldAndValue() {
        logFilter = new LogFilter(null, null, mockStatistics, null, null);

        assertTrue(logFilter.filterByField(mockLogInformation),
            "The filter should pass with null filterField and filterValue.");
    }

    @Test
    @DisplayName("Should throw an exception with unknown filter field")
    void testFilterByField_UnknownFilterField() {
        logFilter = new LogFilter(null, null, mockStatistics, "unknown", "value");

        assertThrows(UnknownFilterException.class, () -> logFilter.filterByField(mockLogInformation),
            "An unknown filter field should throw an exception.");
    }
}
