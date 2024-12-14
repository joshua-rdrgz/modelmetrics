package com.modelmetrics.api.modelmetrics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.modelmetrics.api.modelmetrics.converter.StringAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** User. */
@Entity
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "ID")
  private UUID id;

  @Convert(converter = StringAttributeConverter.class)
  @Column(name = "first_name")
  private String firstName;

  @Convert(converter = StringAttributeConverter.class)
  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @JsonIgnore
  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "verified", nullable = false)
  private boolean verified;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "last_updated_at", nullable = false)
  private LocalDateTime lastUpdatedAt;

  @Column(name = "last_password_change")
  private LocalDateTime lastPasswordChange;

  @Column(name = "tax_allocation_percentage", nullable = false)
  private Integer taxAllocationPercentage;

  @Column(name = "currency", nullable = false)
  private Currency currency;

  /** PrePersist. */
  @PrePersist
  public void prePersist() {
    LocalDateTime currentTime = LocalDateTime.now();
    createdAt = currentTime;
    lastUpdatedAt = currentTime;

    taxAllocationPercentage = 24;
    currency = Currency.getInstance("USD");
  }

  /** PreUpdate. */
  @PreUpdate
  public void preUpdate() {
    lastUpdatedAt = LocalDateTime.now();
  }
}
