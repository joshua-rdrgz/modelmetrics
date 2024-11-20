package com.modelmetrics.api.modelmetrics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/** PasswordResetOtp. */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password_reset_otp")
public class PasswordResetOtp {

  @Id
  @Column(name = "user_id", updatable = false, nullable = false)
  private UUID userId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @Column(name = "otp", nullable = false)
  private String otp;

  @Column(name = "verified", nullable = false)
  private Boolean verified;

  @Column(name = "attempts_remaining", nullable = false)
  private Integer attemptsRemaining;

  @Column(name = "expiry_date", nullable = false)
  private LocalDateTime expiryDate;

  @Column(name = "cooldown_complete")
  private LocalDateTime cooldownComplete;

  /** prePersist. */
  @PrePersist
  public void prePersist() {
    if (attemptsRemaining == null) {
      attemptsRemaining = 3;
    }
    if (verified == null) {
      verified = false;
    }
  }
}
