package org.hl7.davinci.refimpl.patientui.services;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.PayerDto;
import org.hl7.davinci.refimpl.patientui.fhir.FhirCapabilitiesLookup;
import org.hl7.davinci.refimpl.patientui.fhir.model.OAuthUris;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.hl7.davinci.refimpl.patientui.repository.PayerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class PayerService {

  private final PatientDataService patientDataService;
  private final PayerRepository payerRepository;
  private final FhirCapabilitiesLookup fhirCapabilitiesLookup;
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
    Payer payer = modelMapper.map(newPayerDto, Payer.class);
    OAuthUris oAuthUris = fhirCapabilitiesLookup.getOAuthUris(newPayerDto.getFhirServerUri());
    if (oAuthUris.getAuthorize() == null || oAuthUris.getToken() == null) {
      throw new IllegalStateException("The FHIR server does not support OAuth.");
    }
    payer.setAuthorizeUri(oAuthUris.getAuthorize());
    payer.setTokenUri(oAuthUris.getToken());
    return modelMapper.map(payerRepository.save(payer), PayerDto.class);
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
      OAuthUris oAuthUris = fhirCapabilitiesLookup.getOAuthUris(payerDto.getFhirServerUri());
      if (oAuthUris.getAuthorize() == null || oAuthUris.getToken() == null) {
        throw new IllegalStateException("The FHIR server does not support OAuth.");
      }
      payer.setAuthorizeUri(oAuthUris.getAuthorize());
      payer.setTokenUri(oAuthUris.getToken());
    }
    modelMapper.map(payerDto, payer);
    return modelMapper.map(payer, PayerDto.class);
  }

  /**
   * Deletes payer by its id.
   *
   * @param id identification of payer that should be deleted
   */
  @Transactional
  public void deletePayer(Long id) {
    Payer payer = retrievePayer(id);
    patientDataService.clearData(id);
    payerRepository.delete(payer);
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
            String.format("No %s entity with id %s exists!", Payer.class.getSimpleName(), id), 1));
  }
}
