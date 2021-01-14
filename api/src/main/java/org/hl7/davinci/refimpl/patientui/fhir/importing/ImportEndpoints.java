package org.hl7.davinci.refimpl.patientui.fhir.importing;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Holds the {@link IGenericClient}s of the source and target servers used in the FHIR import process.
 *
 * @author Taras Vuyiv
 */
@Getter
@RequiredArgsConstructor
public class ImportEndpoints {

  private final IGenericClient sourceClient;
  private final IGenericClient targetClient;
}
