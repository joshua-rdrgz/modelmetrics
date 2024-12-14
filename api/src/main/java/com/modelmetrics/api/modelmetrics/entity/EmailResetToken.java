package com.modelmetrics.api.modelmetrics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/** EmailResetToken. */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email_reset_token")
public class EmailResetToken {

  @Id
  @Column(name = "user_id", updatable = false, nullable = false)
  private UUID userId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @Column(name = "token")
  private String token;

  @Column(name = "new_email")
  private String newEmail;

  @Column(name = "expiry_date")
  private LocalDateTime expiryDate;
}
