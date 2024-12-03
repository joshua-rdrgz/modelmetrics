package com.modelmetrics.api.modelmetrics.repository.event;

import com.modelmetrics.api.modelmetrics.entity.Event;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** EventRepository. */
@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {}
