package com.modelmetrics.api.modelmetrics.helper;

import com.modelmetrics.api.modelmetrics.entity.User;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** CustomUserDetails. */
public class CustomUserDetails extends User implements UserDetails {

  private Collection<? extends GrantedAuthority> authorities;

  /**
   * CustomUserDetails constructor.
   *
   * @param user the user
   */
  public CustomUserDetails(User user) {
    super(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getPassword(),
        user.isVerified(),
        user.getCreatedAt(),
        user.getLastUpdatedAt(),
        user.getLastPasswordChange());
    this.authorities = new ArrayList<>();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return super.getPassword();
  }

  @Override
  public String getUsername() {
    return super.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
