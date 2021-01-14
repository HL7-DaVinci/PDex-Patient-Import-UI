package org.hl7.davinci.refimpl.patientui.security.payload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class JwtResponse {

  private final String token;
  private final String username;

  private String type = "Bearer";
}
