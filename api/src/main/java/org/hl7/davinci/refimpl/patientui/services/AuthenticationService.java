package org.hl7.davinci.refimpl.patientui.services;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.security.jwt.JwtUtils;
import org.hl7.davinci.refimpl.patientui.security.payload.JwtResponse;
import org.hl7.davinci.refimpl.patientui.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * This service handles the authentication logic.
 *
 * @author Taras Vuyiv
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;

  /**
   * Generates a JWT token for the given user.
   *
   * @param username the username
   * @param password the password
   * @return the {@link JwtResponse}
   */
  public JwtResponse authenticate(String username, String password) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));
    SecurityContextHolder.getContext()
        .setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    return new JwtResponse(jwt, userDetails.getUsername());
  }

  /**
   * Verifies whether the given token is active.
   *
   * @param token the token to verify
   * @return true if token is valid
   */
  public boolean verifyToken(String token) {
    return jwtUtils.validateJwtToken(token);
  }
}
