package org.hl7.davinci.refimpl.patientui.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A DTO representation of the {@link org.hl7.davinci.refimpl.patientui.model.ImportInfo}.
 *
 * @author Taras Vuyiv
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportInfoDto {

  private String resourceType;
  private long createdCount;
  private long updatedCount;
}
