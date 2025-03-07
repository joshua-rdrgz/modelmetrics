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
import com.modelmetrics.api.modelmetrics.util.ResourceFilterer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
      Integer tasksCompleted,
      BigDecimal minTotalMinutesWorked,
      BigDecimal maxTotalMinutesWorked,
      BigDecimal minGrossEarnings,
      BigDecimal maxGrossEarnings,
      BigDecimal minTaxAllocation,
      BigDecimal maxTaxAllocation,
      BigDecimal minNetEarnings,
      BigDecimal maxNetEarnings) {

    List<SessionSummaryDto> filteredSessions =
        new ResourceFilterer<>(sessionRepository.findAll(spec).stream())
            .filterNumberRange(
                session -> session.getGrossEarnings().getAmount(),
                minGrossEarnings,
                maxGrossEarnings)
            .filterEquality(Session::getTasksCompleted, tasksCompleted)
            .filterNumberRange(
                Session::getTotalMinutesWorked, minTotalMinutesWorked, maxTotalMinutesWorked)
            .filterNumberRange(
                session -> session.getTaxAllocation().getAmount(),
                minTaxAllocation,
                maxTaxAllocation)
            .filterNumberRange(
                session -> session.getNetEarnings().getAmount(), minNetEarnings, maxNetEarnings)
            .mapAndCollect(this::convertToSummaryDto);

    // Handle pagination manually
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), filteredSessions.size());
    List<SessionSummaryDto> paginatedSessions = filteredSessions.subList(start, end);

    return new PageImpl<>(paginatedSessions, pageable, filteredSessions.size());
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

  private SessionSummaryDto convertToSummaryDto(Session session) {
    return SessionSummaryDto.builder()
        .id(session.getId())
        .date(session.getCreatedAt().toLocalDate())
        .projectName(session.getProjectName())
        .grossEarnings(session.getGrossEarnings())
        .build();
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
