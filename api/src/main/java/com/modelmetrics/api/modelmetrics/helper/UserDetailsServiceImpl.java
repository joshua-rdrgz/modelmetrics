package com.modelmetrics.api.modelmetrics.helper;

import com.modelmetrics.api.modelmetrics.entity.User;
import com.modelmetrics.api.modelmetrics.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/** UserDetailsServiceImpl. */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired private UserRepository userRepository;

  private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    logger.debug("Entering in loadUserByUsername Method...");

    User user = userRepository.findByEmail(email);

    if (user == null) {
      logger.error("User with email not found: " + email);
      throw new UsernameNotFoundException("could not found user..!!");
    }

    logger.info("User Authenticated Successfully..!!!");

    return new CustomUserDetails(user);
  }
}
