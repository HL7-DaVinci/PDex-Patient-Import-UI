package org.hl7.davinci.refimpl.patientui.security.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequest {

  @NotBlank
  private String username;

  @NotBlank
  private String password;
}
