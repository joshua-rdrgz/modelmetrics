package com.modelmetrics.api.modelmetrics.entity.session;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.modelmetrics.api.modelmetrics.entity.Event;
import com.modelmetrics.api.modelmetrics.helper.session.EventType;
import com.modelmetrics.api.modelmetrics.util.Money;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("SessionCalculatorTest")
class SessionCalculatorTest {

  private static final Currency USD = Currency.getInstance("USD");

  @Nested
  @DisplayName("calculateTasksCompleted")
  class TasksCompletedTests {
    @Builder
    private record TestCase(List<Event> events, int expectedCount) {}

    @ParameterizedTest(name = "{0}")
    @MethodSource("taskCompletedTestCases")
    void calculateTasksCompleted(String description, TestCase testCase) {
      assertEquals(
          testCase.expectedCount, SessionCalculator.calculateTasksCompleted(testCase.events));
    }

    private static Stream<Arguments> taskCompletedTestCases() {
      return Stream.of(
          Arguments.of(
              "Null events list should return 0",
              TestCase.builder().events(null).expectedCount(0).build()),
          Arguments.of(
              "Empty events list should return 0",
              TestCase.builder().events(List.of()).expectedCount(0).build()),
          Arguments.of(
              "2 TASKCOMPLETE events should return 2",
              TestCase.builder()
                  .events(
                      List.of(
                          Event.builder().type(EventType.START).build(),
                          Event.builder().type(EventType.TASKCOMPLETE).build(),
                          Event.builder().type(EventType.TASKCOMPLETE).build(),
                          Event.builder().type(EventType.FINISH).build()))
                  .expectedCount(2)
                  .build()),
          Arguments.of(
              "Only non-TASKCOMPLETE events should return 0",
              TestCase.builder()
                  .events(
                      List.of(
                          Event.builder().type(EventType.START).build(),
                          Event.builder().type(EventType.BREAK).build(),
                          Event.builder().type(EventType.RESUME).build(),
                          Event.builder().type(EventType.FINISH).build()))
                  .expectedCount(0)
                  .build()),
          Arguments.of(
              "Mixed events with 3 TASKCOMPLETE should return 3",
              TestCase.builder()
                  .events(
                      List.of(
                          Event.builder().type(EventType.START).build(),
                          Event.builder().type(EventType.TASKCOMPLETE).build(),
                          Event.builder().type(EventType.BREAK).build(),
                          Event.builder().type(EventType.RESUME).build(),
                          Event.builder().type(EventType.TASKCOMPLETE).build(),
                          Event.builder().type(EventType.TASKCOMPLETE).build(),
                          Event.builder().type(EventType.FINISH).build()))
                  .expectedCount(3)
                  .build()));
    }
  }

  @Nested
  @DisplayName("calculateTotalMinutesWorked")
  class TotalMinutesWorkedTests {
    @Builder
    private record TestCase(List<Event> events, BigDecimal expectedMinutes) {}

    @ParameterizedTest(name = "{0}")
    @MethodSource("minutesWorkedTestCases")
    void calculateTotalMinutesWorked(String description, TestCase testCase) {
      assertEquals(
          testCase.expectedMinutes, SessionCalculator.calculateTotalMinutesWorked(testCase.events));
    }

    private static Stream<Arguments> minutesWorkedTestCases() {
      return Stream.of(
          Arguments.of(
              "Null events list should return 0",
              TestCase.builder().events(null).expectedMinutes(BigDecimal.ZERO).build()),
          Arguments.of(
              "Empty events list should return 0",
              TestCase.builder().events(List.of()).expectedMinutes(BigDecimal.ZERO).build()),
          Arguments.of(
              "Simple 46.67 minute session",
              TestCase.builder()
                  .events(
                      List.of(
                          Event.builder().type(EventType.START).timestamp(1000L).build(),
                          Event.builder().type(EventType.FINISH).timestamp(3800L).build()))
                  .expectedMinutes(new BigDecimal("46.67"))
                  .build()),
          Arguments.of(
              "Session with break totaling 33.33 minutes",
              TestCase.builder()
                  .events(
                      List.of(
                          Event.builder().type(EventType.START).timestamp(1000L).build(),
                          Event.builder().type(EventType.BREAK).timestamp(2000L).build(),
                          Event.builder().type(EventType.RESUME).timestamp(3000L).build(),
                          Event.builder().type(EventType.FINISH).timestamp(4000L).build()))
                  .expectedMinutes(new BigDecimal("33.33"))
                  .build()));
    }
  }

  @Nested
  @DisplayName("calculateGrossEarnings")
  class GrossEarningsTests {
    @Builder
    private record TestCase(BigDecimal minutes, BigDecimal rate, Money expected) {}

    @ParameterizedTest(name = "{0}")
    @MethodSource("grossEarningsTestCases")
    void calculateGrossEarnings(String description, TestCase testCase) {
      assertEquals(
          testCase.expected,
          SessionCalculator.calculateGrossEarnings(testCase.minutes, testCase.rate, USD));
    }

    private static Stream<Arguments> grossEarningsTestCases() {
      return Stream.of(
          Arguments.of(
              "Null inputs should return 0",
              TestCase.builder()
                  .minutes(null)
                  .rate(null)
                  .expected(Money.builder().amount(BigDecimal.ZERO).currency(USD).build())
                  .build()),
          Arguments.of(
              "60 minutes at $100/hr should equal $100.00",
              TestCase.builder()
                  .minutes(new BigDecimal("60"))
                  .rate(new BigDecimal("100"))
                  .expected(Money.builder().amount(new BigDecimal("100.00")).currency(USD).build())
                  .build()),
          Arguments.of(
              "30 minutes at $50/hr should equal $25.00",
              TestCase.builder()
                  .minutes(new BigDecimal("30"))
                  .rate(new BigDecimal("50"))
                  .expected(Money.builder().amount(new BigDecimal("25.00")).currency(USD).build())
                  .build()),
          Arguments.of(
              "0 minutes should equal $0.00",
              TestCase.builder()
                  .minutes(new BigDecimal("0"))
                  .rate(new BigDecimal("100"))
                  .expected(Money.builder().amount(new BigDecimal("0.00")).currency(USD).build())
                  .build()),
          Arguments.of(
              "60 minutes at $0/hr should equal $0.00",
              TestCase.builder()
                  .minutes(new BigDecimal("60"))
                  .rate(new BigDecimal("0"))
                  .expected(Money.builder().amount(new BigDecimal("0.00")).currency(USD).build())
                  .build()),
          Arguments.of(
              "45 minutes at $80/hr should equal $60.00",
              TestCase.builder()
                  .minutes(new BigDecimal("45"))
                  .rate(new BigDecimal("80"))
                  .expected(Money.builder().amount(new BigDecimal("60.00")).currency(USD).build())
                  .build()));
    }
  }

  @Nested
  @DisplayName("calculateTaxAllocation")
  class TaxAllocationTests {
    @Builder
    private record TestCase(Money grossEarnings, double taxRate, Money expected) {}

    @ParameterizedTest(name = "{0}")
    @MethodSource("taxAllocationTestCases")
    void calculateTaxAllocation(String description, TestCase testCase) {
      assertEquals(
          testCase.expected,
          SessionCalculator.calculateTaxAllocation(testCase.grossEarnings, testCase.taxRate));
    }

    private static Stream<Arguments> taxAllocationTestCases() {
      return Stream.of(
          Arguments.of(
              "Null earnings should return 0",
              TestCase.builder()
                  .grossEarnings(null)
                  .taxRate(20.0)
                  .expected(Money.builder().amount(BigDecimal.ZERO).currency(null).build())
                  .build()),
          Arguments.of(
              "$100.00 at 20% tax should equal $20.00",
              TestCase.builder()
                  .grossEarnings(
                      Money.builder().amount(new BigDecimal("100.00")).currency(USD).build())
                  .taxRate(20.0)
                  .expected(Money.builder().amount(new BigDecimal("20.00")).currency(USD).build())
                  .build()),
          Arguments.of(
              "$25.00 at 15% tax should equal $3.75",
              TestCase.builder()
                  .grossEarnings(
                      Money.builder().amount(new BigDecimal("25.00")).currency(USD).build())
                  .taxRate(15.0)
                  .expected(Money.builder().amount(new BigDecimal("3.75")).currency(USD).build())
                  .build()),
          Arguments.of(
              "$0 earnings should equal $0.00 tax",
              TestCase.builder()
                  .grossEarnings(Money.builder().amount(new BigDecimal("0")).currency(USD).build())
                  .taxRate(20.0)
                  .expected(Money.builder().amount(new BigDecimal("0.00")).currency(USD).build())
                  .build()),
          Arguments.of(
              "$100.00 at 0% tax should equal $0.00",
              TestCase.builder()
                  .grossEarnings(
                      Money.builder().amount(new BigDecimal("100.00")).currency(USD).build())
                  .taxRate(0.0)
                  .expected(Money.builder().amount(new BigDecimal("0.00")).currency(USD).build())
                  .build()),
          Arguments.of(
              "$50.00 at 7.5% tax should equal $3.75",
              TestCase.builder()
                  .grossEarnings(
                      Money.builder().amount(new BigDecimal("50.00")).currency(USD).build())
                  .taxRate(7.5)
                  .expected(Money.builder().amount(new BigDecimal("3.75")).currency(USD).build())
                  .build()));
    }
  }

  @Nested
  @DisplayName("calculateNetEarnings")
  class NetEarningsTests {
    @Builder
    private record TestCase(Money grossEarnings, Money taxAllocation, Money expected) {}

    @ParameterizedTest(name = "{0}")
    @MethodSource("netEarningsTestCases")
    void calculateNetEarnings(String description, TestCase testCase) {
      assertEquals(
          testCase.expected,
          SessionCalculator.calculateNetEarnings(testCase.grossEarnings, testCase.taxAllocation));
    }

    private static Stream<Arguments> netEarningsTestCases() {
      return Stream.of(
          Arguments.of(
              "Null inputs should return 0",
              TestCase.builder()
                  .grossEarnings(null)
                  .taxAllocation(null)
                  .expected(Money.builder().amount(BigDecimal.ZERO).currency(null).build())
                  .build()),
          Arguments.of(
              "$100.00 - $20.00 tax should equal $80.00",
              TestCase.builder()
                  .grossEarnings(
                      Money.builder().amount(new BigDecimal("100.00")).currency(USD).build())
                  .taxAllocation(
                      Money.builder().amount(new BigDecimal("20.00")).currency(USD).build())
                  .expected(Money.builder().amount(new BigDecimal("80.00")).currency(USD).build())
                  .build()),
          Arguments.of(
              "$25.00 - $3.75 tax should equal $21.25",
              TestCase.builder()
                  .grossEarnings(
                      Money.builder().amount(new BigDecimal("25.00")).currency(USD).build())
                  .taxAllocation(
                      Money.builder().amount(new BigDecimal("3.75")).currency(USD).build())
                  .expected(Money.builder().amount(new BigDecimal("21.25")).currency(USD).build())
                  .build()),
          Arguments.of(
              "$0 gross should equal $0.00 net",
              TestCase.builder()
                  .grossEarnings(Money.builder().amount(new BigDecimal("0")).currency(USD).build())
                  .taxAllocation(
                      Money.builder().amount(new BigDecimal("10.00")).currency(USD).build())
                  .expected(Money.builder().amount(new BigDecimal("0.00")).currency(USD).build())
                  .build()),
          Arguments.of(
              "$100.00 - $0 tax should equal $100.00",
              TestCase.builder()
                  .grossEarnings(
                      Money.builder().amount(new BigDecimal("100.00")).currency(USD).build())
                  .taxAllocation(Money.builder().amount(new BigDecimal("0")).currency(USD).build())
                  .expected(Money.builder().amount(new BigDecimal("100.00")).currency(USD).build())
                  .build()),
          Arguments.of(
              "$50.00 - $50.00 tax should equal $0.00",
              TestCase.builder()
                  .grossEarnings(
                      Money.builder().amount(new BigDecimal("50.00")).currency(USD).build())
                  .taxAllocation(
                      Money.builder().amount(new BigDecimal("50.00")).currency(USD).build())
                  .expected(Money.builder().amount(new BigDecimal("0.00")).currency(USD).build())
                  .build()));
    }
  }
}
