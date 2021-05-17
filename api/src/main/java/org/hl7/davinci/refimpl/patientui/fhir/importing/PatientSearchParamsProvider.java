package org.hl7.davinci.refimpl.patientui.fhir.importing;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.RuntimeResourceDefinition;
import ca.uhn.fhir.context.RuntimeSearchParam;
import org.hl7.fhir.r4.model.ListResource;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Provides a map of FHIR resources that can have a reference to Patient together with the search parameters that can be
 * used to search for these resources by Patient ID.
 *
 * @author Taras Vuyiv
 */
@Component
public class PatientSearchParamsProvider {

  private final Map<String, List<RuntimeSearchParam>> patientSearchParams;

  public PatientSearchParamsProvider(FhirContext fhirContext) {
    patientSearchParams = Collections.unmodifiableMap(initParamsMap(fhirContext));
  }

  /**
   * @return the map of all the resource types to Patient reference search parameters
   */
  public Map<String, List<RuntimeSearchParam>> getAll() {
    return patientSearchParams;
  }

  /**
   * Creates a map for the given resource types to Patient reference search parameters.
   *
   * @param resourceTypes the resource types to build a map for
   * @return the search parameters map
   */
  public Map<String, List<RuntimeSearchParam>> getForResources(Collection<String> resourceTypes) {
    return resourceTypes.stream()
        .filter(patientSearchParams::containsKey)
        .collect(Collectors.toMap(Function.identity(), patientSearchParams::get));
  }

  private static Map<String, List<RuntimeSearchParam>> initParamsMap(FhirContext fhirContext) {
    Map<String, List<RuntimeSearchParam>> patientParams = new ConcurrentHashMap<>();
    for (String resourceType : fhirContext.getResourceTypes()) {
      List<RuntimeSearchParam> searchParams = new ArrayList<>();
      RuntimeResourceDefinition resourceDefinition = fhirContext.getResourceDefinition(resourceType);
      if (fhirContext.getResourceType(ListResource.class)
          .equals(resourceType)) {
        // For some reason (must be a bug) HAPI does not include the parameters of the List resource to any
        // compartments, so for List we need to add them manually:
        searchParams.add(resourceDefinition.getSearchParam(ListResource.SP_SOURCE));
        searchParams.add(resourceDefinition.getSearchParam(ListResource.SP_PATIENT));
        searchParams.add(resourceDefinition.getSearchParam(ListResource.SP_SUBJECT));
      } else {
        String patientType = fhirContext.getResourceType(Patient.class);
        resourceDefinition.getSearchParamsForCompartmentName(patientType)
            .stream()
            .filter(param -> param.getTargets()
                .contains(patientType))
            .forEach(searchParams::add);
      }
      if (!searchParams.isEmpty()) {
        patientParams.put(resourceType, searchParams);
      }
    }
    return patientParams;
  }
}
