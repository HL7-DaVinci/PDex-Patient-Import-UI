package org.hl7.davinci.refimpl.patientui.fhir.importing;

import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import lombok.Getter;
import org.hl7.fhir.r4.model.Resource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Contains a mapping of the FHIR resource type that can have a reference to the Patient and the search parameters that
 * can be used to perform a search of the resources by the Patient ID.
 *
 * @author Taras Vuyiv
 */
@Getter
public class PatientReference {

  private final Class<? extends Resource> resource;
  private final List<ReferenceClientParam> siblingParams;
  private final List<ReferenceClientParam> soloParams;

  /**
   * Constructor.
   *
   * @param resource      the resource type
   * @param siblingParams the interchangeable search parameters - perform search by the same field
   * @param soloParams    the unique search parameters
   */
  public PatientReference(Class<? extends Resource> resource, List<ReferenceClientParam> siblingParams,
      ReferenceClientParam... soloParams) {
    this(resource, siblingParams, Arrays.asList(soloParams));
  }

  /**
   * Constructor.
   *
   * @param resource      the resource type
   * @param siblingParams the interchangeable search parameters - perform search by the same field
   * @param soloParams    the unique search parameters
   */
  public PatientReference(Class<? extends Resource> resource, List<ReferenceClientParam> siblingParams,
      List<ReferenceClientParam> soloParams) {
    this.resource = resource;
    this.siblingParams = Collections.unmodifiableList(siblingParams);
    this.soloParams = Collections.unmodifiableList(soloParams);
  }
}
