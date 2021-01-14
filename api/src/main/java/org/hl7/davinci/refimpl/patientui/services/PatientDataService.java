package org.hl7.davinci.refimpl.patientui.services;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.ImportInfoDto;
import org.hl7.davinci.refimpl.patientui.dto.ResourceInfoDto;
import org.hl7.davinci.refimpl.patientui.fhir.FhirClientProvider;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.hl7.davinci.refimpl.patientui.repository.ImportInfoRepository;
import org.hl7.davinci.refimpl.patientui.repository.PayerRepository;
import org.hl7.fhir.r4.model.Patient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides abilities to work with the patient data.
 *
 * @author Taras Vuyiv
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PatientDataService {

  private final PayerRepository payerRepository;
  private final ImportInfoRepository importInfoRepository;
  private final FhirClientProvider fhirClientProvider;
  private final ModelMapper modelMapper;

  /**
   * @return a list of all resources ever imported for all payers.
   */
  public List<ResourceInfoDto> getImportedResources() {
    return importInfoRepository.findAllGroupByResourceType();
  }

  /**
   * Returns a list of all resources ever imported for the given payer.
   *
   * @param payerId the ID of the payer
   * @return the {@link ResourceInfoDto} list
   */
  public List<ResourceInfoDto> getImportedResources(Long payerId) {
    Payer payer = payerRepository.getOne(payerId);
    return importInfoRepository.findAllByPayerGroupByResourceType(payer);
  }

  /**
   * Returns a list of resources imported or refreshed at a specific time.
   *
   * @param payerId      the ID of the payer
   * @param importedDate the datetime of an import
   * @return the {@link ImportInfoDto} list
   */
  public List<ImportInfoDto> getImportInfo(Long payerId, OffsetDateTime importedDate) {
    Payer payer = payerRepository.getOne(payerId);
    return importInfoRepository.findAllByPayerAndImportDate(payer, importedDate)
        .parallelStream()
        .map(i -> modelMapper.map(i, ImportInfoDto.class))
        .collect(Collectors.toList());
  }

  /**
   * Removes all the imported data for the given payer.
   *
   * @param payerId the ID of the payer
   */
  @Transactional
  public void clearData(Long payerId) {
    Payer payer = payerRepository.getOne(payerId);
    Set<String> payerResources = importInfoRepository.findDistinctResourcesByPayer(payer);
    payerResources.add(Patient.class.getSimpleName());
    ICriterion<ReferenceClientParam> deleteCriteria = new ReferenceClientParam(Constants.PARAM_SOURCE).hasId(
        payerId.toString());
    IGenericClient localClient = fhirClientProvider.newLocalClient();
    payerResources.forEach(resource -> localClient.delete()
        .resourceConditionalByType(resource)
        .where(deleteCriteria)
        .execute());
    importInfoRepository.deleteAllByPayer(payer);
    payer.setLastImported(null);
    payer.setSourcePatientId(null);
  }
}
