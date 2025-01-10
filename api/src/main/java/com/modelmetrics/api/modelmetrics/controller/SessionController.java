package com.modelmetrics.api.modelmetrics.controller;

import com.modelmetrics.api.modelmetrics.dto.base.SuccessResponse;
import com.modelmetrics.api.modelmetrics.dto.session.SessionDto;
import com.modelmetrics.api.modelmetrics.dto.session.SessionSummaryDto;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.entity.session.Session;
import com.modelmetrics.api.modelmetrics.exception.UnauthorizedSessionAccessException;
import com.modelmetrics.api.modelmetrics.service.session.SessionService;
import com.modelmetrics.api.modelmetrics.specification.SessionSpecifications;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** SessionController. */
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {
  private final SessionService sessionService;

  /** createSessionData. */
  @PostMapping
  public ResponseEntity<SuccessResponse<SessionDto>> createSessionData(
      @AuthenticationPrincipal User user, @Valid @RequestBody SessionDto sessionDto) {
    SessionDto createdSession = sessionService.createSession(user, sessionDto);
    return new ResponseEntity<>(
        new SuccessResponse<>(createdSession, HttpStatus.CREATED.value()), HttpStatus.CREATED);
  }

  /** getAllSessions. */
  @GetMapping
  public ResponseEntity<SuccessResponse<Page<SessionSummaryDto>>> getAllSessions(
      @AuthenticationPrincipal User user,
      Pageable pageable,
      @RequestParam(required = false) String projectName,
      @RequestParam(required = false) LocalDate date,
      @RequestParam(required = false) BigDecimal minGrossEarnings,
      @RequestParam(required = false) BigDecimal maxGrossEarnings) {

    Specification<Session> spec = Specification.where(null);

    if (projectName != null) {
      spec = spec.and(SessionSpecifications.hasProjectName(projectName));
    }
    if (date != null) {
      spec = spec.and(SessionSpecifications.hasDate(date));
    }

    Page<SessionSummaryDto> sessions =
        sessionService.getAllSessionsForUser(
            user, spec, pageable, minGrossEarnings, maxGrossEarnings);

    return new ResponseEntity<>(
        new SuccessResponse<>(sessions, HttpStatus.OK.value()), HttpStatus.OK);
  }

  /** updateSessionData. */
  @PutMapping("/{id}")
  public ResponseEntity<SuccessResponse<SessionDto>> updateSessionData(
      @AuthenticationPrincipal User user,
      @PathVariable UUID id,
      @Valid @RequestBody SessionDto sessionDto) {
    if (!sessionService.isUserAuthorizedForSession(user, id)) {
      throw new UnauthorizedSessionAccessException(
          "User does not have permission to update this session");
    }

    sessionDto.setId(id);
    SessionDto updatedSession = sessionService.updateSession(sessionDto);
    return new ResponseEntity<>(
        new SuccessResponse<>(updatedSession, HttpStatus.OK.value()), HttpStatus.OK);
  }

  /** deleteSessionData. */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSessionData(
      @AuthenticationPrincipal User user, @PathVariable UUID id) {
    if (!sessionService.isUserAuthorizedForSession(user, id)) {
      throw new UnauthorizedSessionAccessException(
          "User does not have permission to delete this session");
    }

    sessionService.deleteSession(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
