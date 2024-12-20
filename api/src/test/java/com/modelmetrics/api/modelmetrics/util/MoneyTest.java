package com.modelmetrics.api.modelmetrics.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.stream.Stream;
import lombok.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("MoneyTest")
class MoneyTest {

  private static final Currency USD = Currency.getInstance("USD");

  @Nested
  @DisplayName("multiply")
  class MultiplyTests {
    @Builder
    private record TestCase(BigDecimal amount, BigDecimal factor, BigDecimal expectedAmount) {}

    @ParameterizedTest(name = "{0}")
    @MethodSource("multiplyTestCases")
    void multiply(String description, TestCase testCase) {
      Money money = Money.builder().amount(testCase.amount).currency(USD).build();
      Money result = money.multiply(testCase.factor);
      assertEquals(testCase.expectedAmount, result.getAmount());
    }

    private static Stream<Arguments> multiplyTestCases() {
      return Stream.of(
          Arguments.of(
              "Multiply 100 by 2",
              TestCase.builder()
                  .amount(new BigDecimal("100"))
                  .factor(new BigDecimal("2"))
                  .expectedAmount(new BigDecimal("200.00"))
                  .build()),
          Arguments.of(
              "Multiply 50 by 0.5",
              TestCase.builder()
                  .amount(new BigDecimal("50"))
                  .factor(new BigDecimal("0.5"))
                  .expectedAmount(new BigDecimal("25.00"))
                  .build()),
          Arguments.of(
              "Multiply 0 by 10",
              TestCase.builder()
                  .amount(new BigDecimal("0"))
                  .factor(new BigDecimal("10"))
                  .expectedAmount(new BigDecimal("0.00"))
                  .build()),
          Arguments.of(
              "Multiply 100 by 0",
              TestCase.builder()
                  .amount(new BigDecimal("100"))
                  .factor(new BigDecimal("0"))
                  .expectedAmount(new BigDecimal("0.00"))
                  .build()));
    }
  }

  @Nested
  @DisplayName("divide")
  class DivideTests {
    @Builder
    private record TestCase(BigDecimal amount, BigDecimal divisor, BigDecimal expectedAmount) {}

    @ParameterizedTest(name = "{0}")
    @MethodSource("divideTestCases")
    void divide(String description, TestCase testCase) {
      Money money = Money.builder().amount(testCase.amount).currency(USD).build();
      Money result = money.divide(testCase.divisor);
      assertEquals(testCase.expectedAmount, result.getAmount());
    }

    private static Stream<Arguments> divideTestCases() {
      return Stream.of(
          Arguments.of(
              "Divide 100 by 2",
              TestCase.builder()
                  .amount(new BigDecimal("100"))
                  .divisor(new BigDecimal("2"))
                  .expectedAmount(new BigDecimal("50.00"))
                  .build()),
          Arguments.of(
              "Divide 50 by 0.5",
              TestCase.builder()
                  .amount(new BigDecimal("50"))
                  .divisor(new BigDecimal("0.5"))
                  .expectedAmount(new BigDecimal("100.00"))
                  .build()),
          Arguments.of(
              "Divide 0 by 10",
              TestCase.builder()
                  .amount(new BigDecimal("0"))
                  .divisor(new BigDecimal("10"))
                  .expectedAmount(new BigDecimal("0.00"))
                  .build()),
          Arguments.of(
              "Divide 100 by 1",
              TestCase.builder()
                  .amount(new BigDecimal("100"))
                  .divisor(new BigDecimal("1"))
                  .expectedAmount(new BigDecimal("100.00"))
                  .build()));
    }
  }

  @Nested
  @DisplayName("subtract")
  class SubtractTests {
    @Builder
    private record TestCase(BigDecimal amount, BigDecimal subtrahend, BigDecimal expectedAmount) {}

    @ParameterizedTest(name = "{0}")
    @MethodSource("subtractTestCases")
    void subtract(String description, TestCase testCase) {
      Money money = Money.builder().amount(testCase.amount).currency(USD).build();
      Money result = money.subtract(testCase.subtrahend);
      assertEquals(testCase.expectedAmount, result.getAmount());
    }

    private static Stream<Arguments> subtractTestCases() {
      return Stream.of(
          Arguments.of(
              "Subtract 50 from 100",
              TestCase.builder()
                  .amount(new BigDecimal("100"))
                  .subtrahend(new BigDecimal("50"))
                  .expectedAmount(new BigDecimal("50.00"))
                  .build()),
          Arguments.of(
              "Subtract 100 from 100",
              TestCase.builder()
                  .amount(new BigDecimal("100"))
                  .subtrahend(new BigDecimal("100"))
                  .expectedAmount(new BigDecimal("0.00"))
                  .build()),
          Arguments.of(
              "Subtract 0 from 100",
              TestCase.builder()
                  .amount(new BigDecimal("100"))
                  .subtrahend(new BigDecimal("0"))
                  .expectedAmount(new BigDecimal("100.00"))
                  .build()),
          Arguments.of(
              "Subtract 150 from 100",
              TestCase.builder()
                  .amount(new BigDecimal("100"))
                  .subtrahend(new BigDecimal("150"))
                  .expectedAmount(new BigDecimal("0.00"))
                  .build()));
    }
  }

  @Nested
  @DisplayName("add")
  class AddTests {
    @Builder
    private record TestCase(BigDecimal amount, BigDecimal addend, BigDecimal expectedAmount) {}

    @ParameterizedTest(name = "{0}")
    @MethodSource("addTestCases")
    void add(String description, TestCase testCase) {
      Money money = Money.builder().amount(testCase.amount).currency(USD).build();
      Money result = money.add(testCase.addend);
      assertEquals(testCase.expectedAmount, result.getAmount());
    }

    private static Stream<Arguments> addTestCases() {
      return Stream.of(
          Arguments.of(
              "Add 50 to 100",
              TestCase.builder()
                  .amount(new BigDecimal("100"))
                  .addend(new BigDecimal("50"))
                  .expectedAmount(new BigDecimal("150.00"))
                  .build()),
          Arguments.of(
              "Add 0 to 100",
              TestCase.builder()
                  .amount(new BigDecimal("100"))
                  .addend(new BigDecimal("0"))
                  .expectedAmount(new BigDecimal("100.00"))
                  .build()),
          Arguments.of(
              "Add -50 to 100",
              TestCase.builder()
                  .amount(new BigDecimal("100"))
                  .addend(new BigDecimal("-50"))
                  .expectedAmount(new BigDecimal("50.00"))
                  .build()),
          Arguments.of(
              "Add 100 to 100",
              TestCase.builder()
                  .amount(new BigDecimal("100"))
                  .addend(new BigDecimal("100"))
                  .expectedAmount(new BigDecimal("200.00"))
                  .build()));
    }
  }
}
