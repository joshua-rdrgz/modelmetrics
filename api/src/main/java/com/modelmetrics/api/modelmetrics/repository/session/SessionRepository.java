package com.modelmetrics.api.modelmetrics.repository.session;

import com.modelmetrics.api.modelmetrics.entity.Session;
import com.modelmetrics.api.modelmetrics.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** SessionRepository. */
@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
  /**
   * Find sessions by user.
   *
   * @param user The user whose sessions are being retrieved.
   * @return A list of sessions belonging to the given user.
   */
  List<Session> findByUser(User user);
}
