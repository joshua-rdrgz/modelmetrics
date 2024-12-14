package com.modelmetrics.api.modelmetrics.task;

import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.repository.user.UserRepository;
import com.modelmetrics.api.modelmetrics.service.email.EmailService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** UnverifiedUserCleanupTask. */
@Component
@RequiredArgsConstructor
public class UnverifiedUserCleanupTask {

  private final UserRepository userRepository;
  private final EmailService emailService;

  /** cleanupUnverifiedUsers. */
  @Scheduled(cron = "0 0 0 * * *") // Run every day at midnight
  public void cleanupUnverifiedUsers() {
    LocalDateTime thresholdDate = LocalDateTime.now().minusDays(30);
    LocalDateTime reminderThresholdDate = LocalDateTime.now().minusDays(23);

    List<User> unverifiedUsers = userRepository.findUnverifiedUsers();

    for (User user : unverifiedUsers) {
      LocalDateTime createdAt = user.getCreatedAt();

      if (createdAt.isBefore(thresholdDate)) {
        userRepository.delete(user);
        emailService.sendAccountDeletedEmail(user.getEmail());
      } else if (createdAt.isBefore(reminderThresholdDate)) {
        emailService.sendAccountVerificationReminderEmail(user.getEmail());
      }
    }
  }
}
