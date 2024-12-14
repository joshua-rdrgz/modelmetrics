package com.modelmetrics.api.modelmetrics.repository.user;

import com.modelmetrics.api.modelmetrics.entity.User;
import java.util.List;
import java.util.UUID;

/** UserRepository. */
public interface UserRepository {

  User save(User user);

  User findByEmail(String email);

  User findFirstById(UUID id);

  void verifyUser(UUID userId);

  List<User> findUnverifiedUsers();

  void delete(User user);
}
