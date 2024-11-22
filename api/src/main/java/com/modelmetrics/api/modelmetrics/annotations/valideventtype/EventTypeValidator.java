package com.modelmetrics.api.modelmetrics.annotations.valideventtype;

import com.modelmetrics.api.modelmetrics.helper.session.EventType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** Validator for the ValidEventType annotation. */
public class EventTypeValidator implements ConstraintValidator<ValidEventType, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true; // Let @NotNull handle null values
    }
    for (EventType eventType : EventType.values()) {
      if (eventType.name().equalsIgnoreCase(value)) {
        return true;
      }
    }
    return false;
  }
}
