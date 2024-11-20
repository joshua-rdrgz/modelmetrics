package com.modelmetrics.api.modelmetrics.repository.emailresettoken.impl;

import com.modelmetrics.api.modelmetrics.entity.EmailResetToken;
import com.modelmetrics.api.modelmetrics.repository.emailresettoken.EmailResetTokenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/** EmailResetTokenRepositoryImpl. */
@Repository
public class EmailResetTokenRepositoryImpl implements EmailResetTokenRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public EmailResetToken save(EmailResetToken emailResetToken) {
    entityManager.persist(emailResetToken);
    return emailResetToken;
  }

  @Override
  public EmailResetToken findByToken(String token) {
    try {
      return entityManager
          .createQuery(
              "SELECT et FROM EmailResetToken et WHERE et.token = :token", EmailResetToken.class)
          .setParameter("token", token)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public EmailResetToken findByUserId(UUID userId) {
    try {
      return entityManager
          .createQuery(
              "SELECT et FROM EmailResetToken et WHERE et.userId = :userId", EmailResetToken.class)
          .setParameter("userId", userId)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public void delete(EmailResetToken emailResetToken) {
    entityManager.remove(emailResetToken);
  }
}
