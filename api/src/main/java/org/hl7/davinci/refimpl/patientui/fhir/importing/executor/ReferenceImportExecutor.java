package org.hl7.davinci.refimpl.patientui.fhir.importing.executor;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hl7.davinci.refimpl.patientui.fhir.FhirCapabilitiesLookup;
import org.hl7.davinci.refimpl.patientui.fhir.importing.ImportEndpoints;
import org.hl7.davinci.refimpl.patientui.fhir.importing.PatientSearchParamsProvider;
import org.hl7.davinci.refimpl.patientui.fhir.importing.ResourceImporter;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.hl7.davinci.refimpl.patientui.repository.ImportInfoRepository;
import org.hl7.davinci.refimpl.patientui.services.progress.ProgressManager;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Imports and refreshes the FHIR resources with the Patient references from source server to target based on the
 * predefined list of resource types that could reference a Patient. See {@link PatientSearchParamsProvider}. A separate
 * FHIR call will be performed for each resource type.
 *
 * @author Taras Vuyiv
 */
@Log4j2
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReferenceImportExecutor implements ImportExecutor {

  private static final int PAGE_SIZE = 20;

  private final FhirCapabilitiesLookup capabilitiesLookup;
  private final ResourceImporter resourceImporter;
  private final PatientSearchParamsProvider patientSearchParams;
  private final ImportInfoRepository importInfoRepository;
  private final ProgressManager progressManager;

  @Override
  public List<MethodOutcome> doImport(ImportEndpoints endpoints, Payer payer) {
    return importReferences(endpoints, prepareImport(endpoints, payer), payer, r -> null);
  }

  @Override
  public List<MethodOutcome> doRefresh(ImportEndpoints endpoints, Payer payer) {
    return refresh(endpoints, payer, prepareImport(endpoints, payer));
  }

  /**
   * Refreshes the patient data for the given resources.
   *
   * @param endpoints     the {@link IGenericClient}s of the source and target import servers
   * @param payer         the {@link Payer} associated with the refresh
   * @param resourceTypes a collection of resource types to perform refresh on
   * @return a {@link MethodOutcome} list of the target server import calls
   */
  public List<MethodOutcome> doRefresh(ImportEndpoints endpoints, Payer payer, Collection<String> resourceTypes) {
    Map<String, List<ReferenceClientParam>> supportedReferences = capabilitiesLookup.getSupportedSearchParams(
        endpoints.getSourceClient(), patientSearchParams.getForResources(resourceTypes));
    return refresh(endpoints, payer, supportedReferences);
  }

  private Map<String, List<ReferenceClientParam>> prepareImport(ImportEndpoints endpoints, Payer payer) {
    Map<String, List<ReferenceClientParam>> supportedReferences = capabilitiesLookup.getSupportedSearchParams(
        endpoints.getSourceClient(), patientSearchParams.getAll());
    resourceImporter.importResource(endpoints, Patient.class, payer.getSourcePatientId(), payer.getId());
    return supportedReferences;
  }

  private List<MethodOutcome> refresh(ImportEndpoints endpoints, Payer payer,
      Map<String, List<ReferenceClientParam>> references) {
    return importReferences(endpoints, references, payer,
        resource -> importInfoRepository.findFirstByResourceTypeOrderByImportDateDesc(resource)
            .map(i -> new DateRangeParam(Date.from(i.getImportDate()
                .toInstant()), null))
            .orElse(null));
  }

  private List<MethodOutcome> importReferences(ImportEndpoints endpoints,
      Map<String, List<ReferenceClientParam>> references, Payer payer, Function<String, DateRangeParam> lastUpdated) {
    Long payerId = payer.getId();
    progressManager.start(payerId, countParams(references));
    return references.entrySet()
        .parallelStream()
        .flatMap(reference -> reference.getValue()
            .parallelStream()
            .map(param -> endpoints.getSourceClient()
                .search()
                .forResource(reference.getKey())
                .where(param.hasId("Patient/" + payer.getSourcePatientId()))
                .lastUpdated(lastUpdated.apply(reference.getKey()))
                .count(PAGE_SIZE)
                .returnBundle(Bundle.class)
                .execute())
            .filter(Objects::nonNull)
            .map(bundle -> {
              List<MethodOutcome> outcomes = resourceImporter.importBundle(endpoints, bundle, payerId);
              progressManager.update(payerId);
              return outcomes;
            })
            .flatMap(List::stream))
        .collect(Collectors.toList());
  }

  private static int countParams(Map<String, List<ReferenceClientParam>> patientReferences) {
    return patientReferences.values()
        .parallelStream()
        .mapToInt(Collection::size)
        .sum();
  }
}
