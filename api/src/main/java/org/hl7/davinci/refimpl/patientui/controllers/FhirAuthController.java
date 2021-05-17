package org.hl7.davinci.refimpl.patientui.controllers;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.controllers.constant.SessionAttributes;
import org.hl7.davinci.refimpl.patientui.fhir.FhirCapabilitiesLookup;
import org.hl7.davinci.refimpl.patientui.fhir.model.OAuthUris;
import org.hl7.davinci.refimpl.patientui.services.FhirAuthService;
import org.hl7.davinci.refimpl.patientui.services.PayerService;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Provides the authorization capabilities for the Payer's FHIR server.
 *
 * @author Taras Vuyiv
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FhirAuthController {

  private final PayerService payerService;
  private final FhirAuthService fhirAuthService;
  private final FhirCapabilitiesLookup fhirCapabilitiesLookup;

  /**
   * Retrieves a {@link CapabilityStatement} from the FHIR server by the given URI, then searches in it for an OAuth URI
   * extension and extracts the authorization and token URIs.
   *
   * @param serverUri the URI of the FHIR server to get auth data from
   * @return authorization and token OAuth URIs
   */
  @GetMapping(value = "/fhir/oauth-uris")
  public OAuthUris getOAuthUris(@RequestParam String serverUri) {
    return fhirCapabilitiesLookup.getOAuthUris(serverUri);
  }

  /**
   * Redirects to the Payer's identity provider in order to receive the authorization code for the FHIR server token
   * access.
   *
   * @param payerId the ID of the Payer patient is about to be authorized
   * @return the authorization {@link RedirectView}
   */
  @GetMapping("/payers/{payerId}/authorize")
  public RedirectView authorizePatient(@PathVariable Long payerId, HttpSession httpSession) {
    String state = UUID.randomUUID()
        .toString();
    httpSession.setAttribute(SessionAttributes.OAUTH_STATE, state);
    String authorizeUrl = fhirAuthService.authorizePatientUrl(payerService.getPayer(payerId), state);
    fhirAuthService.notifyPatientAuthorization(payerId, authorizeUrl);
    return new RedirectView(authorizeUrl);
  }
}
