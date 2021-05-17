package org.hl7.davinci.refimpl.patientui.controllers;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.controllers.constant.SessionAttributes;
import org.hl7.davinci.refimpl.patientui.dto.ImportInfoDto;
import org.hl7.davinci.refimpl.patientui.dto.PayerDto;
import org.hl7.davinci.refimpl.patientui.dto.ResourceInfoDto;
import org.hl7.davinci.refimpl.patientui.dto.ResponseBodyDto;
import org.hl7.davinci.refimpl.patientui.services.ImportRequestService;
import org.hl7.davinci.refimpl.patientui.services.ImportService;
import org.hl7.davinci.refimpl.patientui.services.PatientDataService;
import org.hl7.davinci.refimpl.patientui.services.PayerService;
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

import javax.servlet.http.HttpSession;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * REST API controller to work with information related to payer.
 *
 * @author Kseniia Lutsko
 */
@RestController
@RequestMapping("api/payers")
@RequiredArgsConstructor
public class PayerController {

  private final PayerService payerService;
  private final ImportService importService;
  private final PatientDataService patientDataService;
  private final ImportRequestService importRequestService;

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
  public PayerDto updatePayer(@PathVariable Long id, @Validated @RequestBody PayerDto payerDto) {
    return payerService.updatePayer(id, payerDto);
  }

  /**
   * Endpoint to delete payer item in the database.
   *
   * @param id identification of payer that should be deleted
   */
  @DeleteMapping({"/{id}"})
  @ResponseStatus(value = HttpStatus.ACCEPTED)
  public void deletePayer(@PathVariable Long id) {
    payerService.deletePayer(id);
  }

  /**
   * An endpoint imports the patient data from payer's FHIR server.
   *
   * @param id       the ID of the payer
   * @param authCode the access code to the token of the Payer's server
   */
  @PostMapping({"/{id}/import"})
  @ResponseStatus(value = HttpStatus.ACCEPTED)
  public void importPatientData(@PathVariable Long id, @RequestParam String authCode, @RequestParam String authState,
      HttpSession httpSession) {
    assertAuthState(httpSession, authState);
    importService.importPatientData(id, authCode);
  }

  /**
   * An endpoint refreshes the patient data for a specific payer.
   *
   * @param id            the ID of the payer
   * @param resourceTypes the types of resources to refresh
   * @param authCode      the access code to the token of the Payer's server
   */
  @PostMapping({"/{id}/refresh"})
  @ResponseStatus(value = HttpStatus.ACCEPTED)
  public void refreshPatientData(@PathVariable Long id,
      @RequestParam(name = "resourceType", required = false) List<String> resourceTypes, @RequestParam String authCode,
      @RequestParam String authState, HttpSession httpSession) {
    assertAuthState(httpSession, authState);
    importService.refreshPatientData(id, resourceTypes == null ? Collections.emptyList() : resourceTypes, authCode);
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
   * An endpoint removes all the imported data for all the payers.
   */
  @PostMapping("/clear")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void clearData() {
    patientDataService.clearData(null);
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

  /**
   * Returns the response body of the given Payer request.
   *
   * @param payerId   the ID of the Payer request was made for
   * @param requestId the ID of request
   * @return {@link ResponseBodyDto}
   */
  @GetMapping("/{payerId}/import-requests/{requestId}/content")
  public ResponseBodyDto getImportRequestContent(@PathVariable Long payerId, @PathVariable String requestId) {
    return importRequestService.getResponseBody(payerId, requestId);
  }

  private void assertAuthState(HttpSession httpSession, String authState) {
    if (!Objects.equals(httpSession.getAttribute(SessionAttributes.OAUTH_STATE), authState)) {
      throw new IllegalStateException("The OAuth state value does not match expected.");
    }
    httpSession.removeAttribute(SessionAttributes.OAUTH_STATE);
  }
}
