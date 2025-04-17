package com.modelmetrics.api.modelmetrics.service.session;

import com.modelmetrics.api.modelmetrics.entity.session.Session;
import com.modelmetrics.api.modelmetrics.helper.session.PlatformStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.data.jpa.domain.Specification;

/** SessionFilterParser. */
public class SessionFilterParser {

  /** parseFilter. */
  public static Specification<Session> parseFilter(String filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (filter != null && !filter.isEmpty()) {
        String[] filterParts = filter.split("&");
        for (String part : filterParts) {
          Matcher matcher = Pattern.compile("(<=|>=|<|>)").matcher(part);
          if (matcher.find()) {
            String operator = matcher.group();
            String[] keyValue = part.split(operator);
            if (keyValue.length == 2) {
              String key = keyValue[0];
              String value = keyValue[1];
              predicates.add(createPredicate(key, value, criteriaBuilder, root, operator));
            }
          }
        }
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  /** createPredicate. */
  private static Predicate createPredicate(
      String key,
      String value,
      CriteriaBuilder criteriaBuilder,
      Root<Session> root,
      String operator) {
    switch (key) {
      case "projectName":
        return criteriaBuilder.equal(root.get("projectName"), value);
      case "hourlyRate":
        BigDecimal hourlyRate = new BigDecimal(value);
        switch (operator) {
          case ">":
            return criteriaBuilder.greaterThan(root.get("hourlyRate"), hourlyRate);
          case "<":
            return criteriaBuilder.lessThan(root.get("hourlyRate"), hourlyRate);
          case ">=":
            return criteriaBuilder.greaterThanOrEqualTo(root.get("hourlyRate"), hourlyRate);
          case "<=":
            return criteriaBuilder.lessThanOrEqualTo(root.get("hourlyRate"), hourlyRate);
          default:
            return criteriaBuilder.equal(root.get("hourlyRate"), hourlyRate);
        }
      case "platformStatus":
        PlatformStatus platformStatus = PlatformStatus.valueOf(value);
        return criteriaBuilder.equal(root.get("platformStatus"), platformStatus);
      case "date":
        LocalDate localDate = LocalDate.parse(value);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
        return criteriaBuilder.between(root.get("createdAt"), startOfDay, endOfDay);
      default:
        throw new IllegalArgumentException("Invalid filter key: " + key);
    }
  }
}
