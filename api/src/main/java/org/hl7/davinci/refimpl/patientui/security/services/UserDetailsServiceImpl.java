package org.hl7.davinci.refimpl.patientui.security.services;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * {@link UserDetailsService} implementation.
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDetailsServiceImpl implements UserDetailsService {

  private static final String DEFAULT_USERNAME = "user";
  private static final String DEFAULT_PASSWORD = "user";

  private final PasswordEncoder passwordEncoder;

  /**
   * Checks if username is "user", and if not - does not allow to login.
   *
   * @param username user name
   * @return logged in user
   *
   * @throws UsernameNotFoundException if username is not "user"
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    //Just a temporary solution, with hardcoded user name and password.
    if (!username.equals(DEFAULT_USERNAME)) {
      throw new UsernameNotFoundException("User Not Found with username: " + username);
    }
    return UserDetailsImpl.build(new User(DEFAULT_USERNAME, passwordEncoder.encode(DEFAULT_PASSWORD)));
  }
}
