package org.hl7.davinci.refimpl.patientui.services;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.authorization.OAuthTokenResponse;
import org.hl7.davinci.refimpl.patientui.fhir.client.FhirClientProvider;
import org.hl7.davinci.refimpl.patientui.fhir.importing.ImportEndpoints;
import org.hl7.davinci.refimpl.patientui.fhir.importing.executor.ReferenceImportExecutor;
import org.hl7.davinci.refimpl.patientui.model.ImportInfo;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.hl7.davinci.refimpl.patientui.services.progress.ProgressManager;
import org.hl7.davinci.refimpl.patientui.services.progress.ProgressType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides the import and refresh logic of the patient data.
 *
 * @author Taras Vuyiv
 */
@Service
@RequiredArgsConstructor
public class ImportService {

  private final PayerService payerService;
  private final FhirAuthService authService;
  private final ImportRequestService importRequestService;
  private final ReferenceImportExecutor importExecutor;
  private final FhirClientProvider clientProvider;
  private final ProgressManager progressManager;
  private final FhirContext fhirContext;

  /**
   * Imports the patient data from payer's FHIR server to the local server.
   *
   * @param payerId  the ID of the payer
   * @param authCode the access code to the token of the payer's server
   */
  @Async
  @Transactional
  public void importPatientData(Long payerId, String authCode) {
    progressManager.init(payerId, ProgressType.IMPORT);
    try {
      Payer payer = payerService.retrievePayer(payerId);
      if (payer.getLastImported() != null) {
        throw new IllegalStateException("Patient data already imported. Use refresh instead.");
      }
      OAuthTokenResponse token = authService.retrieveAccessTokenByCode(payer, authCode);
      payer.setSourcePatientId(authService.extractPatient(payer.getFhirServerUri(), token));
      ImportEndpoints importEndpoints = createImportEndpoints(payer, token);
      OffsetDateTime importDate = OffsetDateTime.now();
      List<ImportInfo> importInfo = processImportOutcomes(importExecutor.doImport(importEndpoints, payer), importDate);
      payer.addImportInfo(importInfo);
      payer.setLastImported(importDate);
      payerService.savePayer(payer);
      progressManager.complete(payerId);
    } catch (Exception e) {
      progressManager.fail(payerId, e.getMessage());
      throw e;
    }
  }

  /**
   * Refreshes the patient data for a specific payer.
   *
   * @param payerId       the ID of the payer
   * @param resourceTypes a collection of resource types to refresh. If empty - all the resources will be refreshed
   * @param authCode      the access code to the token of the payer's server
   */
  @Async
  @Transactional
  public void refreshPatientData(Long payerId, List<String> resourceTypes, String authCode) {
    progressManager.init(payerId, ProgressType.REFRESH);
    try {
      Payer payer = payerService.retrievePayer(payerId);
      if (payer.getLastImported() == null) {
        throw new IllegalStateException("Patient data has not been imported yet.");
      }
      importRequestService.deleteAllRequests(payer);
      OAuthTokenResponse token = authService.retrieveAccessTokenByCode(payer, authCode);
      ImportEndpoints importEndpoints = createImportEndpoints(payer, token);
      OffsetDateTime importDate = OffsetDateTime.now();
      List<MethodOutcome> refreshOutcomes = resourceTypes.isEmpty()
          ? importExecutor.doRefresh(importEndpoints, payer)
          : importExecutor.doRefresh(importEndpoints, payer, resourceTypes);
      Date importDateAsDate = Date.from(importDate.toInstant());
      List<MethodOutcome> filteredOutcomes = refreshOutcomes.stream()
          .filter(mo -> mo.getResource()
              .getMeta()
              .getLastUpdated()
              .after(importDateAsDate))
          .collect(Collectors.toList());
      List<ImportInfo> refreshInfo = processImportOutcomes(filteredOutcomes, importDate);
      payer.addImportInfo(refreshInfo);
      payer.setLastImported(importDate);
      payerService.savePayer(payer);
      progressManager.complete(payerId);
    } catch (Exception e) {
      progressManager.fail(payerId, e.getMessage());
      throw e;
    }
  }

  private ImportEndpoints createImportEndpoints(Payer payer, OAuthTokenResponse targetToken) {
    return new ImportEndpoints(clientProvider.newPayerClient(payer, targetToken), clientProvider.newLocalClient());
  }

  private List<ImportInfo> processImportOutcomes(Collection<MethodOutcome> importOutcomes, OffsetDateTime importDate) {
    return importOutcomes.stream()
        .collect(Collectors.groupingBy(mo -> fhirContext.getResourceType(mo.getResource()),
            Collectors.groupingBy(mo -> mo.getCreated() != null, Collectors.counting())))
        .entrySet()
        .parallelStream()
        .map(entry -> {
          ImportInfo importInfo = new ImportInfo();
          importInfo.setResourceType(entry.getKey());
          Map<Boolean, Long> counts = entry.getValue();
          importInfo.setCreatedCount(counts.getOrDefault(Boolean.TRUE, 0L));
          importInfo.setUpdatedCount(counts.getOrDefault(Boolean.FALSE, 0L));
          importInfo.setImportDate(importDate);
          return importInfo;
        })
        .collect(Collectors.toList());
  }
}
