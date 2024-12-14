package com.modelmetrics.api.modelmetrics.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Money. */
@Data
@Builder
@NoArgsConstructor
public class Money {

  private BigDecimal amount;
  private Currency currency;

  /**
   * A setter for the amount field. Adds a max value of BigDecimal.ZERO to the amount and sets the
   * scale to 2 decimal places with a RoundingMode.HALF_UP rounding mode.
   *
   * @param amount BigDecimal
   */
  public void setAmount(BigDecimal amount) {
    if (amount != null) {
      this.amount = amount.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    } else {
      this.amount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
  }

  public Money(BigDecimal amount, Currency currency) {
    setAmount(amount);
    this.currency = currency;
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
    if (totalMinutesWorked == null || hourlyRate == null) {
      return Money.builder().amount(BigDecimal.ZERO).currency(currency).build();
    }
    BigDecimal amount = hourlyRate.multiply(totalMinutesWorked).divide(BigDecimal.valueOf(60));
    return Money.builder().amount(amount).currency(currency).build();
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
    if (grossEarnings == null) {
      return Money.builder().amount(BigDecimal.ZERO).currency(currency).build();
    }
    BigDecimal taxRate =
        BigDecimal.valueOf(taxAllocationPercentage).divide(BigDecimal.valueOf(100));
    BigDecimal amount = grossEarnings.getAmount().multiply(taxRate);
    return Money.builder().amount(amount).currency(currency).build();
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
    if (grossEarnings == null || taxAllocation == null) {
      return Money.builder().amount(BigDecimal.ZERO).currency(currency).build();
    }
    BigDecimal amount = grossEarnings.getAmount().subtract(taxAllocation.getAmount());
    return Money.builder().amount(amount).currency(currency).build();
  }
}
