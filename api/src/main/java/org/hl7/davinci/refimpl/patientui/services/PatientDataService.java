package org.hl7.davinci.refimpl.patientui.services;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.ImportInfoDto;
import org.hl7.davinci.refimpl.patientui.dto.ResourceInfoDto;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.hl7.davinci.refimpl.patientui.repository.ImportInfoRepository;
import org.hl7.davinci.refimpl.patientui.services.cleanup.ImportDataCleaner;
import org.hl7.davinci.refimpl.patientui.services.progress.ProgressManager;
import org.hl7.davinci.refimpl.patientui.services.progress.ProgressType;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides abilities to work with the patient data.
 *
 * @author Taras Vuyiv
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PatientDataService {

  private final ImportDataCleaner importDataCleaner;
  private final PayerService payerService;
  private final ImportInfoRepository importInfoRepository;
  private final ProgressManager progressManager;
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
    Payer payer = payerService.retrievePayer(payerId);
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
    Payer payer = payerService.retrievePayer(payerId);
    return importInfoRepository.findAllByPayerAndImportDate(payer, importedDate)
        .parallelStream()
        .map(i -> modelMapper.map(i, ImportInfoDto.class))
        .collect(Collectors.toList());
  }

  /**
   * Removes all the imported data for the given payer.
   *
   * @param payerId the ID of the payer, if null - the data for all payers will be removed
   */
  @Async
  @Transactional
  public void clearData(Long payerId) {
    Long progressId = payerId == null ? ProgressManager.ALL_PAYERS_PROGRESS_ID : payerId;
    progressManager.init(progressId, ProgressType.CLEAR);
    try {
      List<ResourceInfoDto> resources = payerId == null
          ? importInfoRepository.findAllGroupByResourceType()
          : importInfoRepository.findAllByPayerGroupByResourceType(payerService.retrievePayer(payerId));
      // Adding two more steps for clearing the patient and the local data
      progressManager.start(progressId, (int) resources.stream()
          .mapToLong(ResourceInfoDto::getCount)
          .sum() + 2);
      importDataCleaner.cleanFhirData(payerId, resources);
      importDataCleaner.cleanLocalData(payerId);
      progressManager.update(progressId);
      progressManager.complete(progressId);
    } catch (Exception e) {
      progressManager.fail(progressId, e.getMessage());
      throw e;
    }
  }
}
