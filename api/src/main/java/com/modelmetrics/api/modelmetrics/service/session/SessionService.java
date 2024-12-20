package com.modelmetrics.api.modelmetrics.service.session;

import com.modelmetrics.api.modelmetrics.dto.session.SessionDto;
import com.modelmetrics.api.modelmetrics.entity.User;
import java.util.UUID;

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
