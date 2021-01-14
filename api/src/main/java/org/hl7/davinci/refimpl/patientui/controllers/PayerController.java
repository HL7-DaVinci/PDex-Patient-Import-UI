package org.hl7.davinci.refimpl.patientui.controllers;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.ImportInfoDto;
import org.hl7.davinci.refimpl.patientui.dto.ImportRequestDto;
import org.hl7.davinci.refimpl.patientui.dto.PayerDto;
import org.hl7.davinci.refimpl.patientui.dto.ResourceInfoDto;
import org.hl7.davinci.refimpl.patientui.dto.validation.Import;
import org.hl7.davinci.refimpl.patientui.services.ImportService;
import org.hl7.davinci.refimpl.patientui.services.PatientDataService;
import org.hl7.davinci.refimpl.patientui.services.PayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.groups.Default;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

/**
 * REST API controller to work with information related to payer.
 *
 * @author Kseniia Lutsko
 */
@RestController
@RequestMapping("api/payers")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PayerController {

  private final PayerService payerService;
  private final ImportService importService;
  private final PatientDataService patientDataService;

  /**
   * Endpoint to retrieve list of all payers.
   *
   * @return all payers information.
   */
  @GetMapping
  public List<PayerDto> getPayers() {
    return payerService.getPayers();
  }

  /**
   * Endpoint to retrieve payer by its id.
   *
   * @return payer information
   */
  @GetMapping({"/{id}"})
  public PayerDto getPayer(@PathVariable Long id) {
    return payerService.getPayer(id);
  }

  /**
   * Endpoint to create payer item in the database.
   *
   * @param payerDto the request body DTO
   * @return created payer
   */
  @PostMapping
  @ResponseStatus(value = HttpStatus.CREATED)
  public PayerDto createPayer(@Validated @RequestBody PayerDto payerDto) {
    return payerService.createPayer(payerDto);
  }

  /**
   * Endpoint to update payer item in the database.
   *
   * @param payerDto the request body DTO
   * @param id       identification of payer that should be updated
   * @return updated payer
   */
  @PutMapping({"/{id}"})
  @ResponseStatus(value = HttpStatus.OK)
  public PayerDto updatePayer(@PathVariable Long id, @Validated @RequestBody PayerDto payerDto) {
    return payerService.updatePayer(id, payerDto);
  }

  /**
   * Endpoint to delete payer item in the database.
   *
   * @param id identification of payer that should be deleted
   */
  @DeleteMapping({"/{id}"})
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deletePayer(@PathVariable Long id) {
    payerService.deletePayer(id);
  }

  /**
   * An endpoint imports the patient data from payer's FHIR server.
   *
   * @param id            the ID of the payer
   * @param importRequest holds the access token to the payer's server and the ID of patient to import
   * @return a list of resource types that were imported
   */
  @PostMapping({"/{id}/import"})
  @ResponseStatus(value = HttpStatus.OK)
  public List<ResourceInfoDto> importPatientData(@PathVariable Long id,
      @Validated(value = {Default.class, Import.class}) @RequestBody ImportRequestDto importRequest) {
    return importService.importPatientData(id, importRequest.getPatientId(), importRequest.getAccessToken());
  }

  /**
   * An endpoint refreshes the patient data for a specific payer.
   *
   * @param id            the ID of the payer
   * @param resourceTypes the types of resources to refresh
   * @param importRequest the access token to the payer's server
   * @return a list of resource types that were refreshed
   */
  @PostMapping({"/{id}/refresh"})
  @ResponseStatus(value = HttpStatus.OK)
  public List<ImportInfoDto> refreshPatientData(@PathVariable Long id,
      @RequestParam(name = "resourceType", required = false) List<String> resourceTypes,
      @Valid @RequestBody ImportRequestDto importRequest) {
    return importService.refreshPatientData(id, resourceTypes == null ? Collections.emptyList() : resourceTypes,
        importRequest.getAccessToken());
  }

  /**
   * An endpoint returns a list of all resources ever imported from a specific payer.
   *
   * @param id the ID of the payer
   * @return the {@link ResourceInfoDto} list
   */
  @GetMapping("/{id}/resources")
  public List<ResourceInfoDto> getImportedResources(@PathVariable Long id) {
    return patientDataService.getImportedResources(id);
  }

  /**
   * An endpoint returns a list of resources imported or refreshed at a specific time.
   *
   * @param id   the ID of the payer
   * @param date the datetime of an import
   * @return {@link ImportInfoDto} list
   */
  @GetMapping("/{id}/import-info")
  public List<ImportInfoDto> getImportInfo(@PathVariable Long id,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime date) {
    return patientDataService.getImportInfo(id, date);
  }

  /**
   * An endpoint removes all the imported data for a specific payer.
   *
   * @param id the ID of the payer
   */
  @PostMapping("/{id}/clear")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void clearData(@PathVariable Long id) {
    patientDataService.clearData(id);
  }

  /**
   * An endpoint returns a list of all resources ever imported for all payers.
   *
   * @return {@link ResourceInfoDto} list
   */
  @GetMapping("/resources")
  public List<ResourceInfoDto> getImportedResources() {
    return patientDataService.getImportedResources();
  }
}
