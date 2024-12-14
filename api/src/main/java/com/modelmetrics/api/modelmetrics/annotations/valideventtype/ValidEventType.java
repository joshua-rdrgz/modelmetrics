package com.modelmetrics.api.modelmetrics.annotations.valideventtype;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Custom annotation to validate event types against the EventType enum. */
@Documented
@Constraint(validatedBy = EventTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEventType {
  /** Message to be displayed when validation fails. */
  String message() default "Invalid event type";

  /** Groups for which this validation belongs to. */
  Class<?>[] groups() default {};

  /** Payload containing metadata about the validation. */
  Class<? extends Payload>[] payload() default {};
}
