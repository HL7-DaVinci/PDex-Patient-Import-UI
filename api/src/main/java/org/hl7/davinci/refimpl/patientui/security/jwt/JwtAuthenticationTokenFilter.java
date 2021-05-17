package org.hl7.davinci.refimpl.patientui.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring authorization filter that handles JWT tokens login.
 *
 * @author Mykhailo Stefantsiv
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

  private final JwtAuthenticationProvider authenticationProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      UsernamePasswordAuthenticationToken authentication = authenticationProvider.getAuthentication(
          authorizationHeader);
      if (authentication != null) {
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext()
            .setAuthentication(authentication);
      }
    } catch (Exception e) {
      log.warn("Cannot set user authentication: {}", e);
    }

    filterChain.doFilter(request, response);
  }
}
