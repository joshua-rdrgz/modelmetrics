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
   * Multiplies the amount by a given factor.
   *
   * @param factor The factor to multiply by.
   * @return The resulting Money object.
   */
  public Money multiply(BigDecimal factor) {
    return Money.builder().amount(this.amount.multiply(factor)).currency(this.currency).build();
  }

  /**
   * Divides the amount by a given divisor.
   *
   * @param divisor The divisor to divide by.
   * @return The resulting Money object.
   */
  public Money divide(BigDecimal divisor) {
    return Money.builder()
        .amount(this.amount.divide(divisor, 2, RoundingMode.HALF_UP))
        .currency(this.currency)
        .build();
  }

  /**
   * Subtracts a given amount from the current amount.
   *
   * @param subtrahend The amount to subtract.
   * @return The resulting Money object.
   */
  public Money subtract(BigDecimal subtrahend) {
    return Money.builder().amount(this.amount.subtract(subtrahend)).currency(this.currency).build();
  }

  /**
   * Adds a given amount to the current amount.
   *
   * @param addend The amount to add.
   * @return The resulting Money object.
   */
  public Money add(BigDecimal addend) {
    return Money.builder().amount(this.amount.add(addend)).currency(this.currency).build();
  }
}
