package com.modelmetrics.api.modelmetrics.annotations.valideventorder;

import com.modelmetrics.api.modelmetrics.dto.session.EventDto;
import com.modelmetrics.api.modelmetrics.helper.session.EventType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Validator for the ValidEventOrder annotation. */
public class EventOrderValidator implements ConstraintValidator<ValidEventOrder, List<EventDto>> {

  private static boolean isEventType(EventDto event, EventType type) {
    return type.name().equalsIgnoreCase(event.getType());
  }

  @Override
  public boolean isValid(List<EventDto> events, ConstraintValidatorContext context) {
    if (events == null || events.isEmpty()) {
      return true; // Let @NotEmpty handle empty lists
    }

    List<String> errorMessages = new ArrayList<>();

    try {
      List<EventDto> sortedEvents = getSortedEvents(events);

      if (sortedEvents.size() < 2) {
        errorMessages.add(
            "There must be at least 2 events labeled START and FINISH for the events to be valid."
                + " You have entered only "
                + (sortedEvents.size())
                + " event(s).");
      }

      if (!hasValidStartAndEndEvents(sortedEvents)) {
        errorMessages.add(
            "Events must have a valid START and FINISH event at positions 0 and "
                + (sortedEvents.size() - 1)
                + " respectively.");
      }

      validateEventSequence(sortedEvents, errorMessages);

      if (errorMessages.isEmpty()) {
        return true;
      }

      // Custom error messages present, disable default
      context.disableDefaultConstraintViolation();

      // Add custom error messages
      for (String message : errorMessages) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
      }

      // Fail validation
      return false;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  private List<EventDto> getSortedEvents(List<EventDto> events) {
    return events.stream().sorted(Comparator.comparingLong(EventDto::getTimestamp)).toList();
  }

  private boolean hasValidStartAndEndEvents(List<EventDto> sortedEvents) {
    EventDto firstEvent = sortedEvents.get(0);
    EventDto lastEvent = sortedEvents.get(sortedEvents.size() - 1);
    return isEventType(firstEvent, EventType.START) && isEventType(lastEvent, EventType.FINISH);
  }

  private void validateEventSequence(List<EventDto> sortedEvents, List<String> errorMessages) {
    int breakCount = 0;
    int resumeCount = 0;

    // Loop through middle events (excludes START and FINISH)
    for (int i = 1; i < sortedEvents.size() - 1; i++) {
      EventDto currentEvent = sortedEvents.get(i);
      EventDto nextEvent = sortedEvents.get(i + 1);
      EventDto previousEvent = sortedEvents.get(i - 1);

      EventType currentEventType = EventType.valueOf(currentEvent.getType().toUpperCase());

      int position = i + 1;

      switch (currentEventType) {
        case BREAK:
          breakCount++;
          if (!isEventType(nextEvent, EventType.RESUME)) {
            errorMessages.add(
                "BREAK event at index " + position + " must be followed by a RESUME event.");
          }
          break;
        case RESUME:
          resumeCount++;
          if (!isEventType(previousEvent, EventType.BREAK)) {
            errorMessages.add("RESUME event at index " + position + " must follow a BREAK event.");
          }
          break;
        case TASKCOMPLETE:
          if (isEventType(previousEvent, EventType.BREAK)) {
            errorMessages.add(
                "TASKCOMPLETE event at index " + position + " cannot follow a BREAK event.");
          }
          break;
        default:
          errorMessages.add(
              "Invalid event type at index " + position + ": " + currentEvent.getType());
          break;
      }
    }

    if (breakCount != resumeCount) {
      errorMessages.add("The number of BREAK events must equal the number of RESUME events.");
    }
  }
}
