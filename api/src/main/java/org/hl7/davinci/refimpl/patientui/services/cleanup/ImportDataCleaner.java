package org.hl7.davinci.refimpl.patientui.services.cleanup;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.ResourceInfoDto;
import org.hl7.davinci.refimpl.patientui.fhir.client.FhirClientProvider;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.hl7.davinci.refimpl.patientui.repository.ImportInfoRepository;
import org.hl7.davinci.refimpl.patientui.repository.ImportRequestRepository;
import org.hl7.davinci.refimpl.patientui.repository.PayerRepository;
import org.hl7.davinci.refimpl.patientui.services.progress.ProgressManager;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Knows how to clean the data generated during the import process.
 *
 * @author Taras Vuyiv
 */
@Component
@RequiredArgsConstructor
public class ImportDataCleaner {

  private final PayerRepository payerRepository;
  private final ImportInfoRepository importInfoRepository;
  private final ImportRequestRepository importRequestRepository;
  private final FhirClientProvider clientProvider;
  private final ProgressManager progressManager;

  /**
   * Deletes FHIR resources of the given types associated with the given Payer from the internal FHIR server.
   *
   * @param payerId          the ID of the Payer resources are associated with, if null - data for the all payers will
   *                         be deleted
   * @param resourcesToClean the types of resources to delete
   */
  @Transactional
  public void cleanFhirData(Long payerId, Collection<ResourceInfoDto> resourcesToClean) {
    Long progressId = payerId == null ? ProgressManager.ALL_PAYERS_PROGRESS_ID : payerId;
    IGenericClient client = clientProvider.newLocalClient();
    Set<String> resourceTypes = resourcesToClean.stream()
        .map(ResourceInfoDto::getResourceType)
        .collect(Collectors.toSet());
    resourceTypes.add(Patient.class.getSimpleName());
    resourceTypes.parallelStream()
        .forEach(resource -> {
          IQuery<IBaseBundle> searchQuery = client.search()
              .forResource(resource);
          if (payerId != null) {
            searchQuery.where(new ReferenceClientParam(Constants.PARAM_SOURCE).hasId(payerId.toString()));
          }
          // We retrieve the records IDs first to send delete requests separately for each resource. This prevents the
          // client timeout exceptions during the conditional bulk deletes in case of big number of resources as well as
          // allows to capture much more detailed delete progress
          Bundle bundle = searchQuery.returnBundle(Bundle.class)
              .elementsSubset("id")
              .count(100)
              .execute();
          doCleanFhirBundle(client, bundle, progressId);
        });
  }

  /**
   * Removes the {@link org.hl7.davinci.refimpl.patientui.model.ImportInfo} and {@link
   * org.hl7.davinci.refimpl.patientui.model.ImportRequest} records associated to the given {@link Payer} and clears the
   * source patient ID and imported date values in the {@link Payer} entity itself.
   *
   * @param payerId the {@link Payer} ID to clean data for, if null - the data for all payers will be cleaned
   */
  @Transactional
  public void cleanLocalData(Long payerId) {
    if (payerId != null) {
      Payer payer = payerRepository.getOne(payerId);
      importInfoRepository.deleteAllByPayer(payer);
      importRequestRepository.deleteAllByPayer(payer);
      doCleanPayer(payer);
    } else {
      importInfoRepository.deleteAll();
      importRequestRepository.deleteAll();
      payerRepository.findAll()
          .forEach(this::doCleanPayer);
    }
  }

  private void doCleanFhirBundle(IGenericClient client, Bundle bundle, Long progressId) {
    bundle.getEntry()
        .parallelStream()
        .map(Bundle.BundleEntryComponent::getResource)
        .forEach(resource -> {
          client.delete()
              .resource(resource)
              .execute();
          progressManager.update(progressId);
        });
    Bundle.BundleLinkComponent linkNext = bundle.getLink(IBaseBundle.LINK_NEXT);
    if (linkNext != null) {
      String queryString = URI.create(linkNext.getUrl())
          .getQuery();
      Bundle nextPage = client.loadPage()
          .byUrl(client.getServerBase() + "?" + queryString)
          .andReturnBundle(Bundle.class)
          .execute();
      doCleanFhirBundle(client, nextPage, progressId);
    }
  }

  private void doCleanPayer(Payer payer) {
    payer.setLastImported(null);
    payer.setSourcePatientId(null);
    payerRepository.save(payer);
  }
}
