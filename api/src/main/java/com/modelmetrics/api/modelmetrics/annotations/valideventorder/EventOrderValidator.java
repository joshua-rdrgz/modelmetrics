package com.modelmetrics.api.modelmetrics.annotations.valideventorder;

import com.modelmetrics.api.modelmetrics.dto.session.EventDto;
import com.modelmetrics.api.modelmetrics.helper.session.EventType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
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

    try {
      List<EventDto> sortedEvents = getSortedEvents(events);

      if (sortedEvents.size() < 2) {
        return false;
      }

      boolean hasValidStartAndEnd = hasValidStartAndEndEvents(sortedEvents);
      if (!hasValidStartAndEnd) {
        return false;
      }

      return validateEventSequence(sortedEvents);

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

  private boolean validateEventSequence(List<EventDto> sortedEvents) {
    int breakCount = 0;
    int resumeCount = 0;

    for (int i = 1; i < sortedEvents.size() - 1; i++) {
      EventDto currentEvent = sortedEvents.get(i);
      EventDto nextEvent = sortedEvents.get(i + 1);
      EventDto previousEvent = sortedEvents.get(i - 1);

      EventType currentEventType = EventType.valueOf(currentEvent.getType().toUpperCase());

      switch (currentEventType) {
        case BREAK:
          breakCount++;
          if (!isEventType(nextEvent, EventType.RESUME)) {
            return false;
          }
          break;
        case RESUME:
          resumeCount++;
          if (!isEventType(previousEvent, EventType.BREAK)) {
            return false;
          }
          break;
        case TASKCOMPLETE:
          if (isEventType(previousEvent, EventType.BREAK)) {
            return false;
          }
          break;
        default:
          return false;
      }
    }

    return breakCount == resumeCount;
  }
}
