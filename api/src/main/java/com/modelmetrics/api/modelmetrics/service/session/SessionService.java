package com.modelmetrics.api.modelmetrics.service.session;

import com.modelmetrics.api.modelmetrics.dto.session.SessionDto;
import com.modelmetrics.api.modelmetrics.dto.session.SessionSummaryDto;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.entity.session.Session;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/** SessionService. */
public interface SessionService {

  /**
   * Creates a new session with its associated events.
   *
   * @param user The authenticated user
   * @param sessionDto The session data transfer object
   * @return The created session DTO with calculated fields
   */
  SessionDto createSession(User user, SessionDto sessionDto);

  /**
   * Retrieves a page of sessions for a given user.
   *
   * @param user The authenticated user
   * @param spec The specifications for what to query for
   * @param pageable The pagination information
   * @param minGrossEarnings The min amount a session should have made
   * @param maxGrossEarnings The max amount a session should have made
   * @return A page of session summary DTOs
   */
  Page<SessionSummaryDto> getAllSessionsForUser(
      User user,
      Specification<Session> spec,
      Pageable pageable,
      BigDecimal minGrossEarnings,
      BigDecimal maxGrossEarnings);

  /**
   * Updates an existing session and its associated events.
   *
   * @param sessionDto The session data transfer object
   * @return The updated session DTO with calculated fields
   */
  SessionDto updateSession(SessionDto sessionDto);

  /**
   * Deletes a session and its associated events.
   *
   * @param sessionId The ID of the session to delete
   */
  void deleteSession(UUID sessionId);

  /**
   * Checks if the user is authorized to perform actions on the specified session.
   *
   * @param user The authenticated user
   * @param sessionId The ID of the session
   * @return true if the user is authorized, false otherwise
   */
  boolean isUserAuthorizedForSession(User user, UUID sessionId);
}
