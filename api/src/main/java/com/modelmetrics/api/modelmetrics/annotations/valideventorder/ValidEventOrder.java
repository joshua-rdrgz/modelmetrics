package com.modelmetrics.api.modelmetrics.annotations.valideventorder;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Custom annotation to validate the order and structure of events. */
@Documented
@Constraint(validatedBy = EventOrderValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEventOrder {
  /** Message to be displayed when validation fails. */
  String message() default "Error parsing events";

  /** Groups for which this validation belongs to. */
  Class<?>[] groups() default {};

  /** Payload containing metadata about the validation. */
  Class<? extends Payload>[] payload() default {};
}
