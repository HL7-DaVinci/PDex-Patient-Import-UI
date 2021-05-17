package org.hl7.davinci.refimpl.patientui.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Hold a response body of the request.
 *
 * @author Taras Vuyiv
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBodyDto {

  private String responseBody;
}
