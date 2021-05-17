package org.hl7.davinci.refimpl.patientui.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;

/**
 * Provides the {@link UsernamePasswordAuthenticationToken}.
 *
 * @author Taras Vuyiv
 */
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

  private final UserDetailsService userDetailsService;
  private final JwtUtils jwtUtils;

  /**
   * Creates the {@link UsernamePasswordAuthenticationToken} based on the given JWT authorization header.
   *
   * @param authorizationHeader the JWT authorization header
   * @return the {@link UsernamePasswordAuthenticationToken} or <code>null<code/> if authorization token was not valid
   */
  public UsernamePasswordAuthenticationToken getAuthentication(String authorizationHeader) {
    String jwt = parseJwt(authorizationHeader);
    if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
      return null;
    }
    String username = jwtUtils.getUserNameFromJwtToken(jwt);

    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  private String parseJwt(String authorizationHeader) {
    return StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")
        ? authorizationHeader.substring(7)
        : null;
  }
}
