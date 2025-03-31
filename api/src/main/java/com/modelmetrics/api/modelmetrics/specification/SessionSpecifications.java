package com.modelmetrics.api.modelmetrics.specification;

import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.entity.session.Session;
import com.modelmetrics.api.modelmetrics.helper.session.PlatformStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.data.jpa.domain.Specification;

/** SessionSpecifications. */
public class SessionSpecifications {

  public static Specification<Session> createdByUser(User user) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
  }

  /** hasDate. */
  public static Specification<Session> hasDate(LocalDate date) {
    return (root, query, criteriaBuilder) -> {
      LocalDateTime startOfDay = date.atStartOfDay();
      LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
      return criteriaBuilder.between(root.get("createdAt"), startOfDay, endOfDay);
    };
  }

  /** isBetweenDates. */
  public static Specification<Session> isBetweenDates(LocalDate startDate, LocalDate endDate) {
    return (root, query, criteriaBuilder) -> {
      LocalDateTime start = startDate.atStartOfDay();
      LocalDateTime end = endDate.atTime(LocalTime.MAX);
      return criteriaBuilder.between(root.get("createdAt"), start, end);
    };
  }

  public static Specification<Session> hasProjectName(String projectName) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("projectName"), projectName);
  }

  public static Specification<Session> hasHourlyRate(BigDecimal hourlyRate) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("hourlyRate"), hourlyRate);
  }

  public static Specification<Session> hasPlatformStatus(PlatformStatus platformStatus) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("platformStatus"), platformStatus);
  }
}
