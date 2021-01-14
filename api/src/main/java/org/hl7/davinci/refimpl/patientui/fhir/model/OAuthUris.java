package org.hl7.davinci.refimpl.patientui.fhir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds OAuth authorize and token URIs.
 *
 * @author Taras Vuyiv
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OAuthUris {

  private String authorize;
  private String token;
}
