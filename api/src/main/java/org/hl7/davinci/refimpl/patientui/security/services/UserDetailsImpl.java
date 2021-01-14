package org.hl7.davinci.refimpl.patientui.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.davinci.refimpl.patientui.security.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Simple user details implementation that contains only username and password. It suits for current logic with only one
 * possible user. In case of multiple users - it should be modified.
 */
@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {

  private static final long serialVersionUID = 1731215770547522107L;

  private final String username;
  @JsonIgnore
  private final String password;

  public static UserDetailsImpl build(User user) {
    return new UserDetailsImpl(user.getUsername(), user.getPassword());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(username, user.username);
  }
}
