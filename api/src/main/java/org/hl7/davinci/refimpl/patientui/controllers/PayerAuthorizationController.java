package org.hl7.davinci.refimpl.patientui.controllers;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.PayerDto;
import org.hl7.davinci.refimpl.patientui.services.PayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides the authorization capabilities for the Payer's FHIR server.
 *
 * @author Taras Vuyiv
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PayerAuthorizationController {

  private final PayerService payerService;

  //A temporary solution, in future all the authorization calls should be handled on UI
  @RequestMapping(value = "/payers/{id}/token", method = RequestMethod.GET)
  public ResponseEntity<String> getToken(@PathVariable Long id, @RequestParam String authCode,
      @RequestParam String redirectUri) {
    PayerDto payer = payerService.getPayer(id);
    if (payer.getTokenUri() == null) {
      throw new IllegalStateException("Cannot retrieve token. The Payer does not support OAuth.");
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "authorization_code");
    formData.add("code", authCode);
    formData.add("client_id", payer.getClientId());
    formData.add("redirect_uri", redirectUri);
    return new RestTemplateBuilder().build()
        .postForEntity(payer.getTokenUri(), new HttpEntity<>(formData, headers), String.class);
  }
}
