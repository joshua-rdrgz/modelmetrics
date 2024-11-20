package com.modelmetrics.api.modelmetrics.repository.verificationtoken;

import com.modelmetrics.api.modelmetrics.entity.VerificationToken;
import java.util.UUID;

/** VerificationTokenRepository. */
public interface VerificationTokenRepository {

  VerificationToken save(VerificationToken verificationToken);

  VerificationToken findByToken(String token);

  VerificationToken findByUserId(UUID userId);

  void delete(VerificationToken verificationToken);
}
