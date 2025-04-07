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
import org.springframework.data.jpa.domain.Specification;

/** SessionFilterParser. */
public class SessionFilterParser {
  public static Specification<Session> parseFilter(String filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (filter != null && !filter.isEmpty()) {
        String[] filterParts = filter.split("&");
        for (String part : filterParts) {
          String[] keyValue = part.split("=");
          if (keyValue.length == 2) {
            String keyAndPotentialOperator = keyValue[0];
            String value = keyValue[1];
            if (keyAndPotentialOperator.endsWith(">")) {
              String key =
                  keyAndPotentialOperator.substring(0, keyAndPotentialOperator.length() - 1);
              predicates.add(createPredicate(key, value, criteriaBuilder, root, ">="));
            } else if (keyAndPotentialOperator.endsWith("<")) {
              String key =
                  keyAndPotentialOperator.substring(0, keyAndPotentialOperator.length() - 1);
              predicates.add(createPredicate(key, value, criteriaBuilder, root, "<="));
            } else {
              predicates.add(
                  createPredicate(keyAndPotentialOperator, value, criteriaBuilder, root, "="));
            }
          }
        }
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

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
