package com.modelmetrics.api.modelmetrics.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;

/** ResourceFilterer. */
@AllArgsConstructor
public class ResourceFilterer<T> {
  private Stream<T> stream;

  public ResourceFilterer<T> filterNumberRange(
      Function<T, BigDecimal> valueExtractor, BigDecimal minValue, BigDecimal maxValue) {
    stream = stream.filter(numberInRange(valueExtractor, minValue, maxValue));
    return this;
  }

  public ResourceFilterer<T> filterEquality(
      Function<T, Integer> valueExtractor, Integer targetValue) {
    stream = stream.filter(equalityMatch(valueExtractor, targetValue));
    return this;
  }

  public <R> List<R> mapAndCollect(Function<T, R> mapper) {
    return stream.map(mapper).collect(Collectors.toList());
  }

  /** numberInRange. */
  private Predicate<T> numberInRange(
      Function<T, BigDecimal> valueExtractor, BigDecimal minValue, BigDecimal maxValue) {
    return item -> {
      BigDecimal value = valueExtractor.apply(item);
      return (minValue == null || value.compareTo(minValue) >= 0)
          && (maxValue == null || value.compareTo(maxValue) <= 0);
    };
  }

  private Predicate<T> equalityMatch(Function<T, Integer> valueExtractor, Integer targetValue) {
    return item -> targetValue == null || valueExtractor.apply(item).equals(targetValue);
  }
}
