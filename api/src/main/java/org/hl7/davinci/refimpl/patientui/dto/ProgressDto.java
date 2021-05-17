package org.hl7.davinci.refimpl.patientui.dto;

import lombok.Getter;
import lombok.Setter;
import org.hl7.davinci.refimpl.patientui.services.progress.ProgressStatus;
import org.hl7.davinci.refimpl.patientui.services.progress.ProgressType;

/**
 * A DTO representation of the {@link org.hl7.davinci.refimpl.patientui.services.progress.Progress}.
 *
 * @author Taras Vuyiv
 */
@Getter
@Setter
public class ProgressDto {

  private long id;
  private ProgressType type;
  private int current;
  private int max;
  private ProgressStatus status;
  private String errorMessage;
}
