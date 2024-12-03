package com.modelmetrics.api.modelmetrics.entity;

import com.modelmetrics.api.modelmetrics.helper.session.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/** Event. */
@Entity
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "ID")
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private EventType type;

  @Column(name = "timestamp", nullable = false)
  private Long timestamp;

  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "session_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Session session;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "last_updated_at", nullable = false)
  private LocalDateTime lastUpdatedAt;

  /** prePersist. */
  @PrePersist
  public void prePersist() {
    LocalDateTime currentTime = LocalDateTime.now();
    createdAt = currentTime;
    lastUpdatedAt = currentTime;
    if (session != null) {
      session.preUpdate();
    }
  }

  /** preUpdate. */
  @PreUpdate
  public void preUpdate() {
    lastUpdatedAt = LocalDateTime.now();
    if (session != null) {
      session.preUpdate();
    }
  }
}
