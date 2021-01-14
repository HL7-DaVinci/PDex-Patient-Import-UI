package org.hl7.davinci.refimpl.patientui.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Holds the resource type and number of its records.
 *
 * @author Taras Vuyiv
 */
@Getter
@RequiredArgsConstructor
public class ResourceInfoDto {

  private final String resourceType;
  private final Long count;
}
