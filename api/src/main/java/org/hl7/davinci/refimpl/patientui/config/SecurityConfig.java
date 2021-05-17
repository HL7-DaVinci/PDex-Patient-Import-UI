package org.hl7.davinci.refimpl.patientui.config;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.security.jwt.JwtAuthenticationProvider;
import org.hl7.davinci.refimpl.patientui.security.jwt.JwtAuthenticationTokenFilter;
import org.hl7.davinci.refimpl.patientui.security.jwt.JwtUtils;
import org.hl7.davinci.refimpl.patientui.security.jwt.UnauthorizedEntryPoint;
import org.hl7.davinci.refimpl.patientui.security.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtUtils jwtUtils;
  private final UnauthorizedEntryPoint unauthorizedHandler;

  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
    authenticationManagerBuilder.userDetailsService(userDetailsService())
        .passwordEncoder(passwordEncoder());
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Override
  public UserDetailsService userDetailsService() {
    return new UserDetailsServiceImpl(passwordEncoder());
  }

  @Bean
  public JwtAuthenticationProvider jwtAuthenticationProvider() {
    return new JwtAuthenticationProvider(userDetailsService(), jwtUtils);
  }

  @Bean
  public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
    return new JwtAuthenticationTokenFilter(jwtAuthenticationProvider());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(unauthorizedHandler)
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        //allows API paths for authorization and right debug panel
        .antMatchers("/api/payers/resources", "/api/payers/clear")
        .authenticated()
        .antMatchers("/api/ws/**", "/api/auth/*", "/api/payers", "/api/payers/*", "/api/fhir/oauth-uris",
            "/api/payers/*/authorize")
        .permitAll()
        .antMatchers("/api/**", "/fhir/**")
        .authenticated();

    http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}