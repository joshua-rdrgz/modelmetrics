package com.modelmetrics.api.modelmetrics.repository.verificationtoken.impl;

import com.modelmetrics.api.modelmetrics.entity.VerificationToken;
import com.modelmetrics.api.modelmetrics.repository.verificationtoken.VerificationTokenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** VerificationTokenRepositoryImpl. */
@Repository
@RequiredArgsConstructor
public class VerificationTokenRepositoryImpl implements VerificationTokenRepository {

  @PersistenceContext private EntityManager entityManager;

  public VerificationToken save(VerificationToken verificationToken) {
    entityManager.persist(verificationToken);
    return verificationToken;
  }

  /** findByToken. */
  public VerificationToken findByToken(String token) {
    try {
      TypedQuery<VerificationToken> query =
          entityManager.createQuery(
              "SELECT vt FROM VerificationToken vt WHERE vt.token = :token",
              VerificationToken.class);
      query.setParameter("token", token);
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  /** findByUserId. */
  public VerificationToken findByUserId(UUID userId) {
    try {
      TypedQuery<VerificationToken> query =
          entityManager.createQuery(
              "SELECT vt FROM VerificationToken vt WHERE vt.userId = :userId",
              VerificationToken.class);
      query.setParameter("userId", userId);
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public void delete(VerificationToken verificationToken) {
    entityManager.remove(verificationToken);
  }
}
