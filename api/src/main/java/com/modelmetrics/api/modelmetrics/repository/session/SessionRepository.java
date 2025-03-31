package com.modelmetrics.api.modelmetrics.repository.session;

import com.modelmetrics.api.modelmetrics.entity.session.Session;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/** SessionRepository. */
@Repository
public interface SessionRepository
    extends JpaRepository<Session, UUID>, JpaSpecificationExecutor<Session> {}
