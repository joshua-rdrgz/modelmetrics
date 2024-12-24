package com.modelmetrics.api.modelmetrics.dto.user;

import com.modelmetrics.api.modelmetrics.entity.EmailResetToken;
import com.modelmetrics.api.modelmetrics.entity.PasswordResetOtp;
import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.util.TimeUtil;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;

/** UserDto. */
@Data
@Builder
public class UserDto {

  @NotNull
  @Size(min = 1, max = 50)
  private String firstName;

  @NotNull
  @Size(min = 1, max = 50)
  private String lastName;

  @Null(message = "Email changes not supported in this fashion")
  private String email;

  @NotNull private Currency currency;

  @NotNull
  @Min(0)
  @Max(100)
  private Integer taxAllocationPercentage;

  private boolean pendingEmailChange;
  private String pendingEmail;
  private PasswordResetStatus passwordResetStatus;
  private Optional<Long> cooldownMinsRemaining;

  /** Creates a UserDto from User, EmailResetToken, and PasswordResetOtp. */
  public static UserDto fromUser(
      User user, EmailResetToken emailResetToken, PasswordResetOtp passwordResetOtp) {
    PasswordResetStatus status = PasswordResetStatus.INACTIVE;
    Optional<Long> cooldownMinsRemaining = Optional.empty();

    if (passwordResetOtp != null) {
      if (passwordResetOtp.getVerified()) {
        status = PasswordResetStatus.OTP_VERIFIED;
      } else {
        status = PasswordResetStatus.OTP_SENT;

        // In the case of a cooldown
        if (passwordResetOtp.getCooldownComplete() != null) {
          // Active cooldown exists
          if (passwordResetOtp.getCooldownComplete().isAfter(LocalDateTime.now())) {
            status = PasswordResetStatus.OTP_TIMEOUT_COOLDOWN;

            // Calculate minutes remaining
            cooldownMinsRemaining =
                TimeUtil.minutesBetween(
                    LocalDateTime.now(), passwordResetOtp.getCooldownComplete());
          } else {
            // Cooldown has past, therefore OTP workflow has restarted
            status = PasswordResetStatus.INACTIVE;
          }
        }
      }
    }

    if (user.getLastPasswordChange() != null) {
      LocalDateTime cooldownEnd = user.getLastPasswordChange().plusDays(1);
      if (LocalDateTime.now().isBefore(cooldownEnd)) {
        status = PasswordResetStatus.OTP_COMPLETE_COOLDOWN;
        cooldownMinsRemaining = TimeUtil.minutesBetween(LocalDateTime.now(), cooldownEnd);
      }
    }

    return UserDto.builder()
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .pendingEmailChange(emailResetToken != null)
        .pendingEmail(emailResetToken != null ? emailResetToken.getNewEmail() : null)
        .passwordResetStatus(status)
        .cooldownMinsRemaining(cooldownMinsRemaining)
        .taxAllocationPercentage(user.getTaxAllocationPercentage())
        .currency(user.getCurrency())
        .build();
  }
}
