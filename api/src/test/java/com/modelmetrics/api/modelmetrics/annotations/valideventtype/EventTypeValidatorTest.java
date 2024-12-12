package com.modelmetrics.api.modelmetrics.annotations.valideventtype;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.modelmetrics.api.modelmetrics.helper.session.EventType;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("EventTypeValidator")
class EventTypeValidatorTest {

  private EventTypeValidator validator;

  @Mock private ConstraintValidatorContext context;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    validator = new EventTypeValidator();
  }

  @Nested
  @DisplayName("Valid cases")
  class ValidCases {
    @Test
    @DisplayName("Null value should be valid")
    void nullValueShouldBeValid() {
      assertTrue(validator.isValid(null, context));
    }

    @ParameterizedTest(name = "EventType.{0} should be valid")
    @EnumSource(EventType.class)
    void validEventTypesShouldBeValid(EventType eventType) {
      assertTrue(validator.isValid(eventType.name(), context));
      assertTrue(validator.isValid(eventType.name().toLowerCase(), context));
    }
  }

  @Nested
  @DisplayName("Invalid cases")
  class InvalidCases {
    @ParameterizedTest(name = "{0} should be invalid")
    @EmptySource
    @ValueSource(strings = {"INVALID_TYPE", "RANDOM", "NOT_AN_EVENT"})
    void invalidEventTypesShouldBeInvalid(String invalidType) {
      assertFalse(validator.isValid(invalidType, context));
    }
  }
}
