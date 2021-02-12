package org.hl7.davinci.refimpl.patientui.controllers;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.TokenRequestDto;
import org.hl7.davinci.refimpl.patientui.dto.VerifyTokenResponseDto;
import org.hl7.davinci.refimpl.patientui.security.payload.JwtResponse;
import org.hl7.davinci.refimpl.patientui.security.payload.LoginRequest;
import org.hl7.davinci.refimpl.patientui.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @PostMapping("/login")
  public JwtResponse authenticate(@Valid @RequestBody LoginRequest loginRequest) {
    return authenticationService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
  }

  @PostMapping("/verify")
  public VerifyTokenResponseDto verifyToken(@Valid @RequestBody TokenRequestDto tokenRequest) {
    return new VerifyTokenResponseDto(authenticationService.verifyToken(tokenRequest.getToken()));
  }
}
