package com.modelmetrics.api.modelmetrics.entity;

import com.modelmetrics.api.modelmetrics.helper.session.EventType;
import com.modelmetrics.api.modelmetrics.helper.session.PlatformStatus;
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
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
  @Transient private BigDecimal grossEarnings;
  @Transient private BigDecimal taxAllocation;
  @Transient private BigDecimal netEarnings;

  @PostLoad
  public void postLoad() {
    updateCalculatedFields();
  }

  /** PrePersist. */
  @PrePersist
  public void prePersist() {
    // Default Platform Status
    this.platformStatus = PlatformStatus.PENDING;

    // Created at and Last updated at fields
    LocalDateTime currentTime = LocalDateTime.now();
    createdAt = currentTime;
    lastUpdatedAt = currentTime;
  }

  @PreUpdate
  public void preUpdate() {
    lastUpdatedAt = LocalDateTime.now();
  }

  /** updateCalculatedFields. */
  private void updateCalculatedFields() {
    calculateTasksCompleted();
    calculateTotalMinutesWorked();
    calculateEarnings();
  }

  private void calculateTasksCompleted() {
    this.tasksCompleted =
        (events == null)
            ? 0
            : (int) events.stream().filter(e -> e.getType() == EventType.TASKCOMPLETE).count();
  }

  private void calculateTotalMinutesWorked() {
    if (events == null || events.isEmpty()) {
      this.totalMinutesWorked = BigDecimal.ZERO;
      return;
    }

    long totalSeconds = 0;
    Long startTime = null;
    Long breakStart = null;

    List<Event> sortedEvents =
        events.stream().sorted((e1, e2) -> e1.getTimestamp().compareTo(e2.getTimestamp())).toList();

    for (Event event : sortedEvents) {
      switch (event.getType()) {
        case START:
          startTime = event.getTimestamp();
          break;
        case BREAK:
          if (startTime != null) {
            totalSeconds += event.getTimestamp() - startTime;
            breakStart = event.getTimestamp();
          }
          break;
        case RESUME:
          if (breakStart != null) {
            startTime = event.getTimestamp();
          }
          break;
        case FINISH:
          if (startTime != null) {
            totalSeconds += event.getTimestamp() - startTime;
          }
          break;
        default:
          // TASKCOMPLETE events don't affect time calculation
          break;
      }
    }

    // Convert seconds to minutes
    this.totalMinutesWorked =
        BigDecimal.valueOf(totalSeconds).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
  }

  private void calculateEarnings() {
    if (this.totalMinutesWorked == null || this.hourlyRate == null || this.user == null) {
      this.grossEarnings = BigDecimal.ZERO;
      this.taxAllocation = BigDecimal.ZERO;
      this.netEarnings = BigDecimal.ZERO;
      return;
    }

    // Calculate gross earnings
    this.grossEarnings =
        this.hourlyRate
            .multiply(this.totalMinutesWorked)
            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

    // Calculate tax allocation
    BigDecimal taxRate =
        BigDecimal.valueOf(this.user.getTaxAllocationPercentage())
            .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    this.taxAllocation = this.grossEarnings.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);

    // Calculate net earnings
    this.netEarnings =
        this.grossEarnings.subtract(this.taxAllocation).setScale(2, RoundingMode.HALF_UP);
  }
}
