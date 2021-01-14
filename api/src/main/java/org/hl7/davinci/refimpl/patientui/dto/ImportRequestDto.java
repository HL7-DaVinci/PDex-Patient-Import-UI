package org.hl7.davinci.refimpl.patientui.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hl7.davinci.refimpl.patientui.dto.validation.Import;

import javax.validation.constraints.NotEmpty;

/**
 * A DTO for the import/refresh requests.
 *
 * @author Taras Vuyiv
 */
@Getter
@Setter
@NoArgsConstructor
public class ImportRequestDto {

  @NotEmpty(groups = {Import.class})
  private String patientId;
  private String accessToken;
}
