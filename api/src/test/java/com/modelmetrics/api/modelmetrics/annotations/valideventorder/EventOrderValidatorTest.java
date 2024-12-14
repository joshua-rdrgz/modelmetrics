package com.modelmetrics.api.modelmetrics.annotations.valideventorder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.modelmetrics.api.modelmetrics.dto.session.EventDto;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Test class for EventOrderValidator. */
@DisplayName("Event Order Validator Tests")
@ExtendWith(MockitoExtension.class)
class EventOrderValidatorTest {

  @Builder
  private record EventOrderTestCase(
      String testName, String description, List<EventDto> events, boolean expectedResult) {}

  private EventOrderValidator validator;

  @Mock private ConstraintValidatorContext context;

  private static EventDto event(String type, Long timestamp) {
    return EventDto.builder().type(type).timestamp(timestamp).build();
  }

  @BeforeEach
  void setUp() {
    validator = new EventOrderValidator();
  }

  @Nested
  @DisplayName("Basic Validation Cases")
  class BasicValidationCases {
    private static Stream<Arguments> basicValidationCases() {
      return Stream.of(
              EventOrderTestCase.builder()
                  .testName("Null Events")
                  .description("Should be valid when events are null")
                  .events(null)
                  .expectedResult(true)
                  .build(),
              EventOrderTestCase.builder()
                  .testName("Empty Events")
                  .description("Should be valid when events are empty")
                  .events(List.of())
                  .expectedResult(true)
                  .build(),
              EventOrderTestCase.builder()
                  .testName("Single Event")
                  .description("Should be invalid with only one event")
                  .events(List.of(event("START", 1000L)))
                  .expectedResult(false)
                  .build())
          .map(
              testCase ->
                  Arguments.of(
                      testCase.testName(),
                      testCase.description(),
                      testCase.events(),
                      testCase.expectedResult()));
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("basicValidationCases")
    void testBasicValidation(
        String testName, String description, List<EventDto> events, boolean expectedResult) {
      assertEquals(expectedResult, validator.isValid(events, context), description);
    }
  }

  @Nested
  @DisplayName("Valid Sequence Cases")
  class ValidSequenceCases {
    private static Stream<Arguments> validSequenceCases() {
      return Stream.of(
              EventOrderTestCase.builder()
                  .testName("Simple Start-Finish")
                  .description("Should be valid with start and finish only")
                  .events(List.of(event("START", 1000L), event("FINISH", 2000L)))
                  .expectedResult(true)
                  .build(),
              EventOrderTestCase.builder()
                  .testName("Complex Valid Sequence")
                  .description("Should be valid with all event types in correct order")
                  .events(
                      List.of(
                          event("START", 1000L),
                          event("TASKCOMPLETE", 2000L),
                          event("BREAK", 3000L),
                          event("RESUME", 4000L),
                          event("TASKCOMPLETE", 5000L),
                          event("FINISH", 6000L)))
                  .expectedResult(true)
                  .build(),
              EventOrderTestCase.builder()
                  .testName("Multiple Break-Resume Pairs")
                  .description("Should be valid with multiple break-resume pairs")
                  .events(
                      List.of(
                          event("START", 1000L),
                          event("BREAK", 2000L),
                          event("RESUME", 3000L),
                          event("BREAK", 4000L),
                          event("RESUME", 5000L),
                          event("FINISH", 6000L)))
                  .expectedResult(true)
                  .build(),
              EventOrderTestCase.builder()
                  .testName("Out of Order Timestamps")
                  .description("Should be valid even with out-of-order timestamps")
                  .events(
                      List.of(
                          event("FINISH", 6000L),
                          event("START", 1000L),
                          event("BREAK", 3000L),
                          event("RESUME", 4000L)))
                  .expectedResult(true)
                  .build())
          .map(
              testCase ->
                  Arguments.of(
                      testCase.testName(),
                      testCase.description(),
                      testCase.events(),
                      testCase.expectedResult()));
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("validSequenceCases")
    void testValidSequences(
        String testName, String description, List<EventDto> events, boolean expectedResult) {
      assertEquals(expectedResult, validator.isValid(events, context), description);
    }
  }

  @Nested
  @DisplayName("Invalid Sequence Cases")
  class InvalidSequenceCases {
    private static Stream<Arguments> invalidSequenceCases() {
      return Stream.of(
              EventOrderTestCase.builder()
                  .testName("Missing Start")
                  .description("Should be invalid without START event")
                  .events(
                      List.of(
                          event("BREAK", 1000L), event("RESUME", 2000L), event("FINISH", 3000L)))
                  .expectedResult(false)
                  .build(),
              EventOrderTestCase.builder()
                  .testName("Missing Finish")
                  .description("Should be invalid without FINISH event")
                  .events(
                      List.of(event("START", 1000L), event("BREAK", 2000L), event("RESUME", 3000L)))
                  .expectedResult(false)
                  .build(),
              EventOrderTestCase.builder()
                  .testName("Break Without Resume")
                  .description("Should be invalid when BREAK is not followed by RESUME")
                  .events(
                      List.of(event("START", 1000L), event("BREAK", 2000L), event("FINISH", 3000L)))
                  .expectedResult(false)
                  .build(),
              EventOrderTestCase.builder()
                  .testName("Resume Without Break")
                  .description("Should be invalid when RESUME is not preceded by BREAK")
                  .events(
                      List.of(
                          event("START", 1000L), event("RESUME", 2000L), event("FINISH", 3000L)))
                  .expectedResult(false)
                  .build(),
              EventOrderTestCase.builder()
                  .testName("Task Complete After Break")
                  .description("Should be invalid when TASKCOMPLETE follows BREAK")
                  .events(
                      List.of(
                          event("START", 1000L),
                          event("BREAK", 2000L),
                          event("TASKCOMPLETE", 3000L),
                          event("FINISH", 4000L)))
                  .expectedResult(false)
                  .build(),
              EventOrderTestCase.builder()
                  .testName("Unequal Break-Resume Pairs")
                  .description("Should be invalid with unequal BREAK and RESUME counts")
                  .events(
                      List.of(
                          event("START", 1000L),
                          event("BREAK", 2000L),
                          event("RESUME", 3000L),
                          event("BREAK", 4000L),
                          event("FINISH", 5000L)))
                  .expectedResult(false)
                  .build())
          .map(
              testCase ->
                  Arguments.of(
                      testCase.testName(),
                      testCase.description(),
                      testCase.events(),
                      testCase.expectedResult()));
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("invalidSequenceCases")
    void testInvalidSequences(
        String testName, String description, List<EventDto> events, boolean expectedResult) {
      assertEquals(expectedResult, validator.isValid(events, context), description);
    }
  }

  @Nested
  @DisplayName("Edge Cases")
  class EdgeCases {
    private static Stream<Arguments> edgeCases() {
      return Stream.of(
              EventOrderTestCase.builder()
                  .testName("Null Event Type")
                  .description("Should be invalid with null event type")
                  .events(
                      List.of(event("START", 1000L), event(null, 2000L), event("FINISH", 3000L)))
                  .expectedResult(false)
                  .build(),
              EventOrderTestCase.builder()
                  .testName("Null Timestamp")
                  .description("Should be invalid with null timestamp")
                  .events(
                      List.of(event("START", 1000L), event("BREAK", null), event("FINISH", 3000L)))
                  .expectedResult(false)
                  .build(),
              EventOrderTestCase.builder()
                  .testName("Invalid Event Type")
                  .description("Should be invalid with unrecognized event type")
                  .events(
                      List.of(
                          event("START", 1000L),
                          event("INVALID_TYPE", 2000L),
                          event("FINISH", 3000L)))
                  .expectedResult(false)
                  .build())
          .map(
              testCase ->
                  Arguments.of(
                      testCase.testName(),
                      testCase.description(),
                      testCase.events(),
                      testCase.expectedResult()));
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("edgeCases")
    void testEdgeCases(
        String testName, String description, List<EventDto> events, boolean expectedResult) {
      assertEquals(expectedResult, validator.isValid(events, context), description);
    }
  }
}
