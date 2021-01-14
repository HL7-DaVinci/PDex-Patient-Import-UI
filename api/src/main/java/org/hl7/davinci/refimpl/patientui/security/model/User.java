package org.hl7.davinci.refimpl.patientui.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {

  private String username;
  private String password;
}
