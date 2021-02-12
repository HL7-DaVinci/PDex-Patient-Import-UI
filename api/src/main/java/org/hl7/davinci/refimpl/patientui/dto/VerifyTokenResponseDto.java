package org.hl7.davinci.refimpl.patientui.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A response DTO for the token verification request.
 *
 * @author Taras Vuyiv
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyTokenResponseDto {

  private boolean active;
}
