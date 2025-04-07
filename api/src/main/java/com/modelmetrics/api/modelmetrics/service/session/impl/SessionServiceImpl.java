package com.modelmetrics.api.modelmetrics.service.session.impl;

import com.modelmetrics.api.modelmetrics.dto.session.EventDto;
import com.modelmetrics.api.modelmetrics.dto.session.SessionDto;
import com.modelmetrics.api.modelmetrics.dto.session.SessionSummaryDto;
import com.modelmetrics.api.modelmetrics.entity.Event;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.entity.session.Session;
import com.modelmetrics.api.modelmetrics.helper.session.EventType;
import com.modelmetrics.api.modelmetrics.repository.session.SessionRepository;
import com.modelmetrics.api.modelmetrics.service.session.SessionService;
import com.modelmetrics.api.modelmetrics.service.session.SessionTransientFilterParser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/** SessionServiceImpl. */
@Service
@Transactional
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

  private final SessionRepository sessionRepository;
  private final EntityManager entityManager;

  @Override
  public SessionDto createSession(User user, SessionDto sessionDto) {
    System.out.println("SESSION DTO: " + sessionDto.toString());
    Session session =
        Session.builder()
            .projectName(sessionDto.getProjectName())
            .hourlyRate(sessionDto.getHourlyRate())
            .user(user)
            .events(new ArrayList<>())
            .build();

    List<Event> events =
        sessionDto.getEvents().stream()
            .map(eventDto -> convertToEventEntity(eventDto, session))
            .collect(Collectors.toList());

    session.setEvents(events);
    Session savedSession = sessionRepository.save(session);

    sessionRepository.flush();
    entityManager.clear();

    return convertToDto(
        sessionRepository
            .findById(savedSession.getId())
            .orElseThrow(() -> new EntityNotFoundException("Session not found after creation")));
  }

  @Override
  public Page<SessionSummaryDto> getAllSessionsForUser(
      User user,
      Specification<Session> spec,
      Pageable pageable,
      String transientFilter,
      Set<String> fields) {

    List<Session> sessions = sessionRepository.findAll(spec);

    // Apply transient property filtering
    List<Session> filteredSessions =
        SessionTransientFilterParser.parseTransientFilter(transientFilter).apply(sessions);

    // Map to DTOs
    List<SessionSummaryDto> sessionSummaries =
        filteredSessions.stream()
            .map(session -> convertToSummaryDto(session, fields))
            .collect(Collectors.toList());

    // Handle pagination manually
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), sessionSummaries.size());
    List<SessionSummaryDto> paginatedSummaries = sessionSummaries.subList(start, end);

    return new PageImpl<>(paginatedSummaries, pageable, sessionSummaries.size());
  }

  @Override
  public SessionDto updateSession(SessionDto sessionDto) {
    Session existingSession =
        sessionRepository
            .findById(sessionDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("Session not found"));

    existingSession.setProjectName(sessionDto.getProjectName());
    existingSession.setHourlyRate(sessionDto.getHourlyRate());

    List<Event> newEvents =
        sessionDto.getEvents().stream()
            .map(eventDto -> convertToEventEntity(eventDto, existingSession))
            .collect(Collectors.toList());

    existingSession.getEvents().clear();
    existingSession.getEvents().addAll(newEvents);

    Session updatedSession = sessionRepository.save(existingSession);

    sessionRepository.flush();
    entityManager.clear();

    return convertToDto(
        sessionRepository
            .findById(updatedSession.getId())
            .orElseThrow(() -> new EntityNotFoundException("Session not found after update")));
  }

  @Override
  public void deleteSession(UUID sessionId) {
    Session session =
        sessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new EntityNotFoundException("Session not found"));
    sessionRepository.delete(session);
  }

  @Override
  public boolean isUserAuthorizedForSession(User user, UUID sessionId) {
    return sessionRepository
        .findById(sessionId)
        .map(session -> session.getUser().getId().equals(user.getId()))
        .orElseThrow(() -> new EntityNotFoundException("Session not found"));
  }

  @Override
  public SessionDto getSessionById(UUID sessionId) {
    Session session =
        sessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new EntityNotFoundException("Session not found"));
    return convertToDto(session);
  }

  private SessionDto convertToDto(Session session) {
    return SessionDto.builder()
        .id(session.getId())
        .projectName(session.getProjectName())
        .hourlyRate(session.getHourlyRate())
        .events(session.getEvents().stream().map(this::convertToEventDto).toList())
        .tasksCompleted(session.getTasksCompleted())
        .totalMinutesWorked(session.getTotalMinutesWorked())
        .grossEarnings(session.getGrossEarnings())
        .taxAllocation(session.getTaxAllocation())
        .netEarnings(session.getNetEarnings())
        .build();
  }

  private SessionSummaryDto convertToSummaryDto(Session session, Set<String> fields) {
    SessionSummaryDto.SessionSummaryDtoBuilder builder =
        SessionSummaryDto.builder().id(session.getId());
    Set<String> fieldsCopy = fields == null ? new HashSet<>() : new HashSet<>(fields);

    if (fields == null || fields.contains("date")) {
      builder.date(session.getCreatedAt().toLocalDate());
      fieldsCopy.remove("date");
    }
    if (fields == null || fields.contains("projectName")) {
      builder.projectName(session.getProjectName());
      fieldsCopy.remove("projectName");
    }
    if (fields == null || fields.contains("grossEarnings")) {
      builder.grossEarnings(session.getGrossEarnings());
      fieldsCopy.remove("grossEarnings");
    }
    if (fields != null && fields.contains("hourlyRate")) {
      builder.hourlyRate(session.getHourlyRate());
      fieldsCopy.remove("hourlyRate");
    }
    if (fields != null && fields.contains("tasksCompleted")) {
      builder.tasksCompleted(session.getTasksCompleted());
      fieldsCopy.remove("tasksCompleted");
    }
    if (fields != null && fields.contains("totalMinutesWorked")) {
      builder.totalMinutesWorked(session.getTotalMinutesWorked());
      fieldsCopy.remove("totalMinutesWorked");
    }
    if (fields != null && fields.contains("taxAllocation")) {
      builder.taxAllocation(session.getTaxAllocation());
      fieldsCopy.remove("taxAllocation");
    }
    if (fields != null && fields.contains("netEarnings")) {
      builder.netEarnings(session.getNetEarnings());
      fieldsCopy.remove("netEarnings");
    }
    if (fields != null && fields.contains("events")) {
      builder.events(session.getEvents().stream().map(this::convertToEventDto).toList());
      fieldsCopy.remove("events");
    }

    if (!fieldsCopy.isEmpty()) {
      throw new IllegalArgumentException(
          "Invalid fields: "
              + String.join(", ", fieldsCopy)
              + ". Valid fields are: date, projectName, grossEarnings, hourlyRate, tasksCompleted,"
              + " totalMinutesWorked, taxAllocation, netEarnings, events");
    }

    return builder.build();
  }

  private Event convertToEventEntity(EventDto dto, Session session) {
    return Event.builder()
        .type(EventType.valueOf(dto.getType().toUpperCase()))
        .timestamp(dto.getTimestamp())
        .session(session)
        .build();
  }

  private EventDto convertToEventDto(Event event) {
    return new EventDto(event.getType().name(), event.getTimestamp());
  }
}
