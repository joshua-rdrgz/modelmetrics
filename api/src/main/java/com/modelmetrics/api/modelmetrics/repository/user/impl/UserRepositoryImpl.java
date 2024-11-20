package com.modelmetrics.api.modelmetrics.repository.user.impl;

import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.entity.VerificationToken;
import com.modelmetrics.api.modelmetrics.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/** UserRepositoryImpl. */
@Repository
public class UserRepositoryImpl implements UserRepository {

  @PersistenceContext private EntityManager entityManager;

  public User save(User user) {
    entityManager.persist(user);
    return user;
  }

  /** findByEmail. */
  public User findByEmail(String email) {
    try {
      TypedQuery<User> query =
          entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
      query.setParameter("email", email);
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public User findFirstById(UUID id) {
    return entityManager.find(User.class, id);
  }

  /** verifyUser. */
  public void verifyUser(UUID userId) {
    User user = findFirstById(userId);
    if (user != null) {
      if (user.isVerified()) {
        return;
      }

      VerificationToken verificationToken;
      try {
        verificationToken =
            entityManager
                .createQuery(
                    "SELECT vt FROM VerificationToken vt WHERE vt.userId = :userId",
                    VerificationToken.class)
                .setParameter("userId", userId)
                .getSingleResult();
      } catch (NoResultException e) {
        // Verification token not found for unverified user, do not proceed
        return;
      }

      user.setVerified(true);
      entityManager.merge(user);
      entityManager.remove(verificationToken);
    }
  }

  /** findUnverifiedUsers. */
  public List<User> findUnverifiedUsers() {
    try {
      TypedQuery<User> query =
          entityManager.createQuery("SELECT u FROM User u WHERE u.verified = false", User.class);
      return query.getResultList();
    } catch (NoResultException e) {
      return Collections.emptyList();
    }
  }

  public void delete(User user) {
    entityManager.remove(user);
  }
}
