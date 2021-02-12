package org.hl7.davinci.refimpl.patientui.config;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.security.jwt.AuthenticationJwtTokenFilter;
import org.hl7.davinci.refimpl.patientui.security.jwt.JwtUtils;
import org.hl7.davinci.refimpl.patientui.security.jwt.UnauthorizedEntryPoint;
import org.hl7.davinci.refimpl.patientui.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtUtils jwtUtils;
  private final UnauthorizedEntryPoint unauthorizedHandler;

  @Bean
  public AuthenticationJwtTokenFilter authenticationJwtTokenFilter() {
    return new AuthenticationJwtTokenFilter(jwtUtils, userDetailsService());
  }

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
        .antMatchers("/api/payers/resources")
        .authenticated()
        .antMatchers("/api/auth/*", "/api/payers", "/api/payers/*")
        .permitAll()
        .antMatchers("/api/**", "/fhir/**")
        .authenticated();

    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}