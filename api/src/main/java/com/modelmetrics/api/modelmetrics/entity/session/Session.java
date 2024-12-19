package com.modelmetrics.api.modelmetrics.entity.session;

import com.modelmetrics.api.modelmetrics.entity.Event;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.helper.session.PlatformStatus;
import com.modelmetrics.api.modelmetrics.util.Money;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/** Session. */
@Entity
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "session")
public class Session {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "ID")
  private UUID id;

  @Column(name = "project_name", nullable = false)
  private String projectName;

  @Column(name = "hourly_rate", nullable = false)
  private BigDecimal hourlyRate;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @OneToMany(
      mappedBy = "session",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  @OrderBy("timestamp ASC")
  private List<Event> events;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "last_updated_at", nullable = false)
  private LocalDateTime lastUpdatedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "platform_status", nullable = false)
  private PlatformStatus platformStatus;

  // DERIVED VALUES
  @Transient private Integer tasksCompleted;
  @Transient private BigDecimal totalMinutesWorked;
  @Transient private Money grossEarnings;
  @Transient private Money taxAllocation;
  @Transient private Money netEarnings;

  /** postLoad. */
  @PostLoad
  public void postLoad() {
    this.tasksCompleted = SessionCalculator.calculateTasksCompleted(this.events);
    this.totalMinutesWorked = SessionCalculator.calculateTotalMinutesWorked(this.events);

    this.grossEarnings =
        SessionCalculator.calculateGrossEarnings(
            totalMinutesWorked, hourlyRate, this.user.getCurrency());
    this.taxAllocation =
        SessionCalculator.calculateTaxAllocation(
            grossEarnings, this.user.getTaxAllocationPercentage());
    this.netEarnings = SessionCalculator.calculateNetEarnings(grossEarnings, taxAllocation);
  }

  /** prePersist. */
  @PrePersist
  public void prePersist() {
    this.platformStatus = PlatformStatus.PENDING;
    LocalDateTime currentTime = LocalDateTime.now();
    createdAt = currentTime;
    lastUpdatedAt = currentTime;
  }

  @PreUpdate
  public void preUpdate() {
    lastUpdatedAt = LocalDateTime.now();
  }
}
