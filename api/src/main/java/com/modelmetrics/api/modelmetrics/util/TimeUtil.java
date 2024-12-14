package com.modelmetrics.api.modelmetrics.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/** TimeUtil. */
public class TimeUtil {
  public static Optional<Long> minutesBetween(LocalDateTime from, LocalDateTime to) {
    return Optional.of(ChronoUnit.MINUTES.between(from, to));
  }
}
