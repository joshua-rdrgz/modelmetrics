package com.modelmetrics.api.modelmetrics.repository.session;

import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.entity.session.Session;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** SessionRepository. */
@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
  /**
   * Find sessions by user with pagination.
   *
   * @param user The user whose sessions are being retrieved.
   * @param pageable The pagination information.
   * @return A page of sessions belonging to the given user.
   */
  Page<Session> findByUser(User user, Pageable pageable);
}
