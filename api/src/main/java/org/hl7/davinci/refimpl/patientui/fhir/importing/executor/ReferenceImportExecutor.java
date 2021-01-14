package org.hl7.davinci.refimpl.patientui.fhir.importing.executor;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.DateRangeParam;
import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.fhir.FhirCapabilitiesLookup;
import org.hl7.davinci.refimpl.patientui.fhir.importing.ImportEndpoints;
import org.hl7.davinci.refimpl.patientui.fhir.importing.PatientReference;
import org.hl7.davinci.refimpl.patientui.fhir.importing.PatientReferenceProvider;
import org.hl7.davinci.refimpl.patientui.fhir.importing.ResourceImporter;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.hl7.davinci.refimpl.patientui.repository.ImportInfoRepository;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Imports and refreshes the FHIR resources with the Patient references from source server to target based on the
 * predefined list of resource types that could reference a Patient. See {@link PatientReferenceProvider}. A separate
 * FHIR call will be performed for each resource type.
 *
 * @author Taras Vuyiv
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReferenceImportExecutor implements ImportExecutor {

  private final FhirCapabilitiesLookup capabilitiesLookup;
  private final ResourceImporter resourceImporter;
  private final PatientReferenceProvider patientReferenceProvider;
  private final ImportInfoRepository importInfoRepository;

  @Override
  public List<MethodOutcome> doImport(ImportEndpoints endpoints, Payer payer) {
    resourceImporter.importResource(endpoints, Patient.class, payer.getSourcePatientId(), payer.getId());
    return importReferences(endpoints, patientReferenceProvider.getAll(), payer, r -> null);
  }

  @Override
  public List<MethodOutcome> doRefresh(ImportEndpoints endpoints, Payer payer) {
    resourceImporter.importResource(endpoints, Patient.class, payer.getSourcePatientId(), payer.getId());
    return refresh(endpoints, payer, patientReferenceProvider.getAll());
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
    List<PatientReference> patientReferences = resourceTypes.parallelStream()
        .map(patientReferenceProvider::getByResource)
        .collect(Collectors.toList());
    return refresh(endpoints, payer, patientReferences);
  }

  private List<MethodOutcome> refresh(ImportEndpoints endpoints, Payer payer,
      Collection<PatientReference> patientReferences) {
    return importReferences(endpoints, patientReferences, payer,
        resource -> importInfoRepository.findFirstByResourceTypeOrderByImportDateDesc(resource.getSimpleName())
            .map(i -> new DateRangeParam(Date.from(i.getImportDate()
                .toInstant()), null))
            .orElse(null));
  }

  private List<MethodOutcome> importReferences(ImportEndpoints endpoints, Collection<PatientReference> references,
      Payer payer, Function<Class<? extends Resource>, DateRangeParam> lastUpdated) {
    IGenericClient sourceClient = endpoints.getSourceClient();
    CapabilityStatement sourceCapabilities = sourceClient.capabilities()
        .ofType(CapabilityStatement.class)
        .execute();
    return references.parallelStream()
        .map(reference -> capabilitiesLookup.getSupportedSearchParams(sourceCapabilities, reference)
            .parallelStream()
            .map(param -> sourceClient.search()
                .forResource(reference.getResource())
                .where(param.hasId(Patient.class.getSimpleName() + "/" + payer.getSourcePatientId()))
                .lastUpdated(lastUpdated.apply(reference.getResource()))
                .returnBundle(Bundle.class)
                .execute())
            .map(bundle -> resourceImporter.importBundle(endpoints, bundle, payer.getId()))
            .flatMap(List::stream))
        .flatMap(Function.identity())
        .collect(Collectors.toList());
  }
}
