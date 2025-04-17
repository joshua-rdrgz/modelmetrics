package com.modelmetrics.api.modelmetrics.service.session;

import com.modelmetrics.api.modelmetrics.dto.session.SessionDto;
import com.modelmetrics.api.modelmetrics.dto.session.SessionSummaryDto;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.entity.session.Session;
import java.util.Set;
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
   * Retrieves all sessions for a user with filtering and dynamic field selection.
   *
   * @param user The authenticated user
   * @param specification Specification for database filtering
   * @param pageable Pagination information
   * @param transientFilter Filter for transient fields
   * @param fields Set of field names to include in the response
   * @return Page of SessionSummaryDto with selected fields
   */
  Page<SessionSummaryDto> getAllSessionsForUser(
      User user,
      Specification<Session> specification,
      Pageable pageable,
      String transientFilter,
      Set<String> fields);

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

  /**
   * Retrieves a session by its ID.
   *
   * @param sessionId The ID of the session to retrieve
   * @return The session DTO with calculated fields
   * @throws EntityNotFoundException if the session is not found
   */
  SessionDto getSessionById(UUID sessionId);
}
