package org.hl7.davinci.refimpl.patientui.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hl7.davinci.refimpl.patientui.dto.PayerDto;
import org.hl7.davinci.refimpl.patientui.dto.ResourceInfoDto;
import org.hl7.davinci.refimpl.patientui.fhir.FhirCapabilitiesLookup;
import org.hl7.davinci.refimpl.patientui.fhir.model.OAuthUris;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.hl7.davinci.refimpl.patientui.repository.ImportInfoRepository;
import org.hl7.davinci.refimpl.patientui.repository.PayerRepository;
import org.hl7.davinci.refimpl.patientui.services.cleanup.ImportDataCleaner;
import org.hl7.davinci.refimpl.patientui.services.progress.ProgressManager;
import org.hl7.davinci.refimpl.patientui.services.progress.ProgressType;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Works with payer information.
 *
 * @author Kseniia Lutsko
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayerService {

  private final PayerRepository payerRepository;
  private final ImportInfoRepository importInfoRepository;
  private final FhirCapabilitiesLookup fhirCapabilitiesLookup;
  private final ProgressManager progressManager;
  private final ImportDataCleaner importDataCleaner;
  private final ModelMapper modelMapper;

  /**
   * Retrieves payers from the database.
   *
   * @return all payers from the database
   */
  public List<PayerDto> getPayers() {
    return payerRepository.findAll()
        .parallelStream()
        .map(payer -> modelMapper.map(payer, PayerDto.class))
        .collect(Collectors.toList());
  }

  /**
   * Retrieves payer from the database by id.
   *
   * @return found payer by id from the database
   */
  public PayerDto getPayer(Long id) {
    return modelMapper.map(retrievePayer(id), PayerDto.class);
  }

  /**
   * Creates and saves payer in the database.
   *
   * @param newPayerDto new payer
   * @return created payer
   */
  @Transactional
  public PayerDto createPayer(PayerDto newPayerDto) {
    updateOAuthUris(newPayerDto);
    Payer payer = modelMapper.map(newPayerDto, Payer.class);
    return modelMapper.map(savePayer(payer), PayerDto.class);
  }

  /**
   * Updates payer in the database.
   *
   * @param payerDto payer data
   * @param id       identification of payer that should be updated
   * @return updated payer
   */
  @Transactional
  public PayerDto updatePayer(Long id, PayerDto payerDto) {
    Payer payer = retrievePayer(id);
    if (!Objects.equals(payer.getFhirServerUri(), payerDto.getFhirServerUri())) {
      if (payer.getLastImported() != null) {
        throw new IllegalStateException("The Payer's server URI cannot be updated when data were already imported.");
      }
      updateOAuthUris(payerDto);
    }
    modelMapper.map(payerDto, payer);
    return modelMapper.map(payer, PayerDto.class);
  }

  /**
   * Deletes payer by its id.
   *
   * @param id identification of payer that should be deleted
   */
  @Async
  @Transactional
  public void deletePayer(Long id) {
    progressManager.init(id, ProgressType.DELETE);
    try {
      Payer payer = retrievePayer(id);
      List<ResourceInfoDto> resources = importInfoRepository.findAllByPayerGroupByResourceType(payer);
      progressManager.start(id, (int) resources.stream()
          .mapToLong(ResourceInfoDto::getCount)
          .sum() + 2);
      importDataCleaner.cleanFhirData(id, resources);
      payerRepository.delete(payer);
      progressManager.update(id);
      progressManager.complete(id);
      progressManager.delete(id);
    } catch (Exception e) {
      progressManager.fail(id, e.getMessage());
      throw e;
    }
  }

  /**
   * Retrieves the {@link Payer} entity by its ID.
   *
   * @param id the ID of the {@link Payer}
   * @return the {@link Payer} entity
   * @throws EmptyResultDataAccessException in case no records found by the given ID
   */
  Payer retrievePayer(Long id) {
    return payerRepository.findById(id)
        .orElseThrow(() -> new EmptyResultDataAccessException(
            String.format("No %s entity with id '%s' exists!", Payer.class.getSimpleName(), id), 1));
  }

  /**
   * Persists the given payer in the database.
   *
   * @param payer the {@link Payer} to persist
   * @return persisted {@link Payer}
   */
  Payer savePayer(Payer payer) {
    return payerRepository.save(payer);
  }

  private void updateOAuthUris(PayerDto payer) {
    if (StringUtils.isAnyBlank(payer.getTokenUri(), payer.getAuthorizeUri())) {
      OAuthUris oAuthUris = fhirCapabilitiesLookup.getOAuthUris(payer.getFhirServerUri());
      if (StringUtils.isAnyBlank(oAuthUris.getToken(), oAuthUris.getAuthorize())) {
        throw new IllegalStateException("The FHIR server does not support OAuth.");
      }
      payer.setAuthorizeUri(oAuthUris.getAuthorize());
      payer.setTokenUri(oAuthUris.getToken());
    }
  }
}
