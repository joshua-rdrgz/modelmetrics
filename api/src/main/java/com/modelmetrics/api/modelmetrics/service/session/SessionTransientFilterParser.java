package com.modelmetrics.api.modelmetrics.service.session;

import com.modelmetrics.api.modelmetrics.entity.session.Session;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** SessionTransientFilterParser. */
public class SessionTransientFilterParser {

  public static Function<List<Session>, List<Session>> parseTransientFilter(
      String transientFilter) {
    return sessions -> {
      if (transientFilter == null || transientFilter.isEmpty()) {
        return sessions;
      }

      List<Session> filteredSessions = new ArrayList<>(sessions);
      String[] filterParts = transientFilter.split("&");

      for (String part : filterParts) {
        // Extract operator and value from the filter part
        String keyAndPotentialOperator;
        String value;

        // Check if the part contains an equals sign
        if (part.contains("=")) {
          String[] keyValue = part.split("=");
          keyAndPotentialOperator = keyValue[0];
          value = keyValue[1];
        } else {
          // Handle cases where there's no equals sign (e.g., "grossEarnings<220")
          // Find the first occurrence of an operator
          int operatorIndex = -1;
          for (int i = 0; i < part.length(); i++) {
            if (part.charAt(i) == '<' || part.charAt(i) == '>' || part.charAt(i) == '=') {
              operatorIndex = i;
              break;
            }
          }

          if (operatorIndex != -1) {
            keyAndPotentialOperator = part.substring(0, operatorIndex + 1); // Include the operator
            value = part.substring(operatorIndex + 1);
          } else {
            // If no operator found, skip this part
            continue;
          }
        }

        filteredSessions = applyTransientFilter(keyAndPotentialOperator, value, filteredSessions);
      }

      return filteredSessions;
    };
  }

  private static List<Session> applyTransientFilter(
      String keyAndPotentialOperator, String value, List<Session> sessions) {
    String operator = "=";
    String key = keyAndPotentialOperator;

    if (keyAndPotentialOperator.endsWith(">")) {
      operator = ">=";
      key = keyAndPotentialOperator.substring(0, keyAndPotentialOperator.length() - 1);
    } else if (keyAndPotentialOperator.endsWith("<")) {
      operator = "<=";
      key = keyAndPotentialOperator.substring(0, keyAndPotentialOperator.length() - 1);
    }

    return filterByTransientProperty(key, value, operator, sessions);
  }

  private static List<Session> filterByTransientProperty(
      String key, String value, String operator, List<Session> sessions) {
    List<Session> result = new ArrayList<>();

    switch (key) {
      case "tasksCompleted":
        Integer tasksCompleted = Integer.valueOf(value);
        result = filterByComparison(sessions, Session::getTasksCompleted, tasksCompleted, operator);
        break;
      case "totalMinutesWorked":
        BigDecimal totalMinutesWorked = new BigDecimal(value);
        result =
            filterByComparison(
                sessions, Session::getTotalMinutesWorked, totalMinutesWorked, operator);
        break;
      case "grossEarnings":
        BigDecimal grossEarningsAmount = new BigDecimal(value);
        result =
            filterByComparison(
                sessions,
                session -> session.getGrossEarnings().getAmount(),
                grossEarningsAmount,
                operator);
        break;
      case "taxAllocation":
        BigDecimal taxAllocationAmount = new BigDecimal(value);
        result =
            filterByComparison(
                sessions,
                session -> session.getTaxAllocation().getAmount(),
                taxAllocationAmount,
                operator);
        break;
      case "netEarnings":
        BigDecimal netEarningsAmount = new BigDecimal(value);
        result =
            filterByComparison(
                sessions,
                session -> session.getNetEarnings().getAmount(),
                netEarningsAmount,
                operator);
        break;
      default:
        throw new IllegalArgumentException("Invalid transient filter key: " + key);
    }

    return result;
  }

  private static <T extends Comparable<T>> List<Session> filterByComparison(
      List<Session> sessions, Function<Session, T> extractor, T value, String operator) {
    List<Session> result = new ArrayList<>();

    for (Session session : sessions) {
      T extractedValue = extractor.apply(session);
      boolean matches = false;

      switch (operator) {
        case ">":
          matches = extractedValue.compareTo(value) > 0;
          break;
        case "<":
          matches = extractedValue.compareTo(value) < 0;
          break;
        case ">=":
          matches = extractedValue.compareTo(value) >= 0;
          break;
        case "<=":
          matches = extractedValue.compareTo(value) <= 0;
          break;
        default: // Equals
          matches = extractedValue.compareTo(value) == 0;
          break;
      }

      if (matches) {
        result.add(session);
      }
    }

    return result;
  }
}
