package com.modelmetrics.api.modelmetrics.repository.emailresettoken;

import com.modelmetrics.api.modelmetrics.entity.EmailResetToken;
import java.util.UUID;

/** EmailResetTokenRepository. */
public interface EmailResetTokenRepository {

  EmailResetToken save(EmailResetToken emailResetToken);

  EmailResetToken findByToken(String token);

  EmailResetToken findByUserId(UUID userId);

  void delete(EmailResetToken emailResetToken);
}
