package org.hl7.davinci.refimpl.patientui.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.hl7.davinci.refimpl.patientui.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Generates JWT tokens.
 *
 * @author Mykhailo Stefantsiv
 */
@Component
@Slf4j
public class JwtUtils {

  @Value("${app.jwtSecret}")
  private String jwtSecret;

  @Value("${app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateJwtToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
    Date issuedAt = new Date();
    return JWT.create()
        .withSubject(userPrincipal.getUsername())
        .withIssuedAt(issuedAt)
        .withExpiresAt(new Date(issuedAt.getTime() + jwtExpirationMs))
        .sign(Algorithm.HMAC256(jwtSecret));
  }

  public String getUserNameFromJwtToken(String token) {
    return JWT.decode(token)
        .getSubject();
  }

  /**
   * Handles all main possible errors in passed JWT tokens.
   *
   * @param authToken JWT token string.
   * @return true, if valid
   */
  public boolean validateJwtToken(String authToken) {
    try {
      JWT.require(Algorithm.HMAC256(jwtSecret))
          .build()
          .verify(authToken);
      return true;
    } catch (JWTVerificationException e) {
      log.error("JWT token verification failed: {}", e.getMessage());
    }
    return false;
  }
}
