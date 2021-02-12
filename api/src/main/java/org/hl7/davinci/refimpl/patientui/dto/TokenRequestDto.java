package org.hl7.davinci.refimpl.patientui.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * A request DTO containing an access token.
 *
 * @author Taras Vuyiv
 */
@Getter
@Setter
public class TokenRequestDto {

  @NotEmpty
  private String token;
}
