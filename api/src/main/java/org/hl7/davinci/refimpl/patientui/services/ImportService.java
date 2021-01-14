package org.hl7.davinci.refimpl.patientui.services;

import ca.uhn.fhir.rest.api.MethodOutcome;
import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.ImportInfoDto;
import org.hl7.davinci.refimpl.patientui.dto.ResourceInfoDto;
import org.hl7.davinci.refimpl.patientui.fhir.FhirClientProvider;
import org.hl7.davinci.refimpl.patientui.fhir.importing.ImportEndpoints;
import org.hl7.davinci.refimpl.patientui.fhir.importing.executor.ReferenceImportExecutor;
import org.hl7.davinci.refimpl.patientui.model.ImportInfo;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ImportService {

  private final PayerService payerService;
  private final ReferenceImportExecutor importExecutor;
  private final FhirClientProvider clientProvider;
  private final ModelMapper modelMapper;

  /**
   * Imports the patient data from payer's FHIR server to the local server.
   *
   * @param payerId     the ID of the payer
   * @param patientId   the ID of patient to import
   * @param accessToken the access token to the payer's server. Can be null if source server is not secured
   * @return a list of resource types that were imported
   */
  @Transactional
  public List<ResourceInfoDto> importPatientData(Long payerId, String patientId, String accessToken) {
    OffsetDateTime importDate = OffsetDateTime.now();
    Payer payer = payerService.retrievePayer(payerId);
    if (payer.getLastImported() != null) {
      throw new IllegalStateException("Patient data already imported. Use refresh instead.");
    }
    payer.setSourcePatientId(patientId);
    ImportEndpoints importEndpoints = createImportEndpoints(payer.getFhirServerUri(), accessToken);
    List<ImportInfo> importInfo = processImportOutcomes(importExecutor.doImport(importEndpoints, payer), importDate);
    payer.addImportInfo(importInfo);
    payer.setLastImported(importDate);
    return importInfo.parallelStream()
        .map(i -> new ResourceInfoDto(i.getResourceType(), i.getCreatedCount()))
        .collect(Collectors.toList());
  }

  /**
   * Refreshes the patient data for a specific payer.
   *
   * @param payerId       the ID of the payer
   * @param resourceTypes a collection of resource types to refresh. If empty - all the resources will be refreshed
   * @param accessToken   the access token to the payer's server. Can be null if source server is not secured
   * @return a list of resource types that were refreshed
   */
  @Transactional
  public List<ImportInfoDto> refreshPatientData(Long payerId, List<String> resourceTypes, String accessToken) {
    OffsetDateTime importDate = OffsetDateTime.now();
    Payer payer = payerService.retrievePayer(payerId);
    if (payer.getLastImported() == null) {
      throw new IllegalStateException("Patient data has not been imported yet.");
    }
    ImportEndpoints importEndpoints = createImportEndpoints(payer.getFhirServerUri(), accessToken);
    List<MethodOutcome> refreshOutcomes = resourceTypes.isEmpty()
        ? importExecutor.doRefresh(importEndpoints, payer)
        : importExecutor.doRefresh(importEndpoints, payer, resourceTypes);
    List<ImportInfo> refreshInfo = processImportOutcomes(refreshOutcomes, importDate);
    payer.addImportInfo(refreshInfo);
    payer.setLastImported(importDate);
    return refreshInfo.parallelStream()
        .map(i -> modelMapper.map(i, ImportInfoDto.class))
        .collect(Collectors.toList());
  }

  private ImportEndpoints createImportEndpoints(String targetServerUri, String targetToken) {
    return new ImportEndpoints(clientProvider.newSecuredClient(targetServerUri, targetToken),
        clientProvider.newLocalClient());
  }

  private List<ImportInfo> processImportOutcomes(Collection<MethodOutcome> importOutcomes, OffsetDateTime importDate) {
    return importOutcomes.stream()
        .filter(mo -> mo.getResource() != null)
        .filter(mo -> mo.getResource()
            .getMeta()
            .getLastUpdated()
            .after(Date.from(importDate.toInstant())))
        .collect(Collectors.groupingBy(mo -> mo.getResource()
            .getClass()
            .getSimpleName(), Collectors.groupingBy(mo -> mo.getCreated() != null, Collectors.counting())))
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
