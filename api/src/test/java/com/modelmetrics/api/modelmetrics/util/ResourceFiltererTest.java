package com.modelmetrics.api.modelmetrics.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** ResourceFiltererTest. */
public class ResourceFiltererTest {

  private static final List<Integer> NUMBERS = IntStream.range(1, 11).boxed().toList();

  /** testFilterNumberRange. */
  @ParameterizedTest
  @MethodSource("numberRangeTestCases")
  public void testFilterNumberRange(BigDecimal minValue, BigDecimal maxValue, int expectedSize) {
    // Act
    List<Integer> filteredNumbers =
        new ResourceFilterer<>(NUMBERS.stream())
            .filterNumberRange(n -> BigDecimal.valueOf(n), minValue, maxValue)
            .mapAndCollect(Function.identity());

    // Assert
    assertNotNull(filteredNumbers);
    assertEquals(expectedSize, filteredNumbers.size());
  }

  private static Stream<Arguments> numberRangeTestCases() {
    return Stream.of(
        // minValue, maxValue, expectedSize
        Arguments.of(BigDecimal.valueOf(5), BigDecimal.valueOf(10), 6), // Regular range
        Arguments.of(null, BigDecimal.valueOf(10), 10), // Null minValue
        Arguments.of(BigDecimal.valueOf(5), null, 6), // Null maxValue
        Arguments.of(null, null, 10), // Both null
        Arguments.of(BigDecimal.valueOf(11), BigDecimal.valueOf(15), 0), // Out of range
        Arguments.of(BigDecimal.valueOf(1), BigDecimal.valueOf(1), 1), // Single value
        Arguments.of(BigDecimal.valueOf(3), BigDecimal.valueOf(7), 5) // Mid-range
        );
  }

  /** testFilterEquality. */
  @ParameterizedTest
  @MethodSource("equalityTestCases")
  public void testFilterEquality(Integer targetValue, int expectedSize) {
    // Act
    List<Integer> filteredNumbers =
        new ResourceFilterer<>(NUMBERS.stream())
            .filterEquality(Function.identity(), targetValue)
            .mapAndCollect(Function.identity());

    // Assert
    assertNotNull(filteredNumbers);
    assertEquals(expectedSize, filteredNumbers.size());
  }

  private static Stream<Arguments> equalityTestCases() {
    return Stream.of(
        // targetValue, expectedSize
        Arguments.of(5, 1), // Regular equality match
        Arguments.of(null, 10), // Null target value
        Arguments.of(11, 0), // No match
        Arguments.of(1, 1), // Lower bound
        Arguments.of(10, 1) // Upper bound
        );
  }

  @Test
  public void testMultipleFilters() {
    // Act
    List<Integer> filteredNumbers =
        new ResourceFilterer<>(NUMBERS.stream())
            .filterNumberRange(
                n -> BigDecimal.valueOf(n), BigDecimal.valueOf(3), BigDecimal.valueOf(8))
            .filterEquality(n -> n % 2, 1) // Only odd numbers
            .mapAndCollect(Function.identity());

    // Assert
    assertNotNull(filteredNumbers);
    assertEquals(3, filteredNumbers.size()); // Only 3, 5, 7 should match
  }

  /** testCustomMapper. */
  @ParameterizedTest
  @MethodSource("customMapperTestCases")
  public void testCustomMapper(Function<Integer, ?> mapper, Object expectedFirstElement) {
    // Act
    List<?> result =
        new ResourceFilterer<>(NUMBERS.stream())
            .filterNumberRange(n -> BigDecimal.valueOf(n), BigDecimal.valueOf(5), null)
            .mapAndCollect(mapper);

    // Assert
    assertNotNull(result);
    assertEquals(6, result.size());
    assertEquals(expectedFirstElement, result.get(0));
  }

  private static Stream<Arguments> customMapperTestCases() {
    return Stream.of(
        // mapper, expectedFirstElement
        Arguments.of((Function<Integer, String>) n -> "Number: " + n, "Number: 5"),
        Arguments.of((Function<Integer, Double>) n -> n * 1.5, 7.5),
        Arguments.of((Function<Integer, Boolean>) n -> n % 2 == 0, false));
  }
}
