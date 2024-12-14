package com.modelmetrics.api.modelmetrics.entity.session;

import com.modelmetrics.api.modelmetrics.entity.Event;
import com.modelmetrics.api.modelmetrics.helper.session.EventType;
import com.modelmetrics.api.modelmetrics.util.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Calculator for Session-related computations. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SessionCalculator {

  /**
   * Calculates the number of completed tasks in a list of events.
   *
   * @param events The list of events to analyze.
   * @return The number of completed tasks.
   */
  public static int calculateTasksCompleted(List<Event> events) {
    return (events == null)
        ? 0
        : (int) events.stream().filter(e -> e.getType() == EventType.TASKCOMPLETE).count();
  }

  /**
   * Calculates the total minutes worked based on a list of events.
   *
   * @param events The list of events to analyze.
   * @return The total minutes worked as a BigDecimal.
   */
  public static BigDecimal calculateTotalMinutesWorked(List<Event> events) {
    if (events == null || events.isEmpty()) {
      return BigDecimal.ZERO;
    }

    long totalSeconds = 0;
    Long startTime = null;
    Long breakStart = null;

    List<Event> sortedEvents =
        events.stream().sorted((e1, e2) -> e1.getTimestamp().compareTo(e2.getTimestamp())).toList();

    for (Event event : sortedEvents) {
      switch (event.getType()) {
        case START:
          startTime = event.getTimestamp();
          break;
        case BREAK:
          if (startTime != null) {
            totalSeconds += event.getTimestamp() - startTime;
            breakStart = event.getTimestamp();
          }
          break;
        case RESUME:
          if (breakStart != null) {
            startTime = event.getTimestamp();
          }
          break;
        case FINISH:
          if (startTime != null) {
            totalSeconds += event.getTimestamp() - startTime;
          }
          break;
        default:
          break;
      }
    }

    return BigDecimal.valueOf(totalSeconds).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
  }

  /**
   * Calculates the gross earnings based on total minutes worked and hourly rate.
   *
   * @param totalMinutesWorked The total minutes worked.
   * @param hourlyRate The hourly rate.
   * @param currency The currency to use for the calculation.
   * @return The gross earnings as a Money object.
   */
  public static Money calculateGrossEarnings(
      BigDecimal totalMinutesWorked, BigDecimal hourlyRate, Currency currency) {
    return Money.calculateGrossEarnings(totalMinutesWorked, hourlyRate, currency);
  }

  /**
   * Calculates the tax allocation based on gross earnings and tax rate.
   *
   * @param grossEarnings The gross earnings.
   * @param taxAllocationPercentage The tax allocation percentage.
   * @param currency The currency to use for the calculation.
   * @return The tax allocation as a Money object.
   */
  public static Money calculateTaxAllocation(
      Money grossEarnings, double taxAllocationPercentage, Currency currency) {
    return Money.calculateTaxAllocation(grossEarnings, taxAllocationPercentage, currency);
  }

  /**
   * Calculates the net earnings based on gross earnings and tax allocation.
   *
   * @param grossEarnings The gross earnings.
   * @param taxAllocation The tax allocation.
   * @param currency The currency to use for the calculation.
   * @return The net earnings as a Money object, never less than 0.00.
   */
  public static Money calculateNetEarnings(
      Money grossEarnings, Money taxAllocation, Currency currency) {
    return Money.calculateNetEarnings(grossEarnings, taxAllocation, currency);
  }
}
