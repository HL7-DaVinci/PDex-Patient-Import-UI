package org.hl7.davinci.refimpl.patientui.fhir.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.authorization.OAuthTokenResponse;
import org.hl7.davinci.refimpl.patientui.fhir.interceptor.OAuthTokenInterceptor;
import org.hl7.davinci.refimpl.patientui.fhir.interceptor.PayerRequestInterceptor;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.hl7.davinci.refimpl.patientui.security.payload.JwtResponse;
import org.hl7.davinci.refimpl.patientui.services.LocalAuthService;
import org.hl7.davinci.refimpl.patientui.services.FhirAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Provides FHIR {@link IGenericClient}s handling the security.
 *
 * @author Taras Vuyiv
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FhirClientProvider {

  private final FhirContext fhirContext;
  private final Environment environment;
  private final LocalAuthService authenticationService;
  private final PayerRequestInterceptor requestInterceptor;
  private final FhirAuthService fhirAuthService;

  /**
   * Create a FHIR client working with the internally running FHIR server.
   *
   * @return the {@link IGenericClient}
   */
  public IGenericClient newLocalClient() {
    // TODO: Schema needs to be externalized or database populated directly.
    // In future there will be no need in sample data since everything will be imported directly from Payer Sandboxes.
    String localClientUri = "https://localhost:" + environment.getProperty("local.server.port") + "/fhir";
    IGenericClient localClient = fhirContext.newRestfulGenericClient(localClientUri);
    // Since we are calling our own secured endpoint - a security tokenResponse needs to be provided.
    JwtResponse jwtResponse = authenticationService.authenticate("user", "user");
    localClient.registerInterceptor(new BearerTokenAuthInterceptor(jwtResponse.getToken()));
    return localClient;
  }

  /**
   * Creates a FHIR client working with the Payer FHIR server with the given URI.
   *
   * @param payer the Payer this client is created for
   * @param token the {@link OAuthTokenResponse}. If null, a client will skip the authorization header
   * @return the {@link IGenericClient}
   */
  public IGenericClient newPayerClient(Payer payer, OAuthTokenResponse token) {
    PayerAwareClientFactory clientFactory = (PayerAwareClientFactory) fhirContext.getRestfulClientFactory();
    IGenericClient payerClient = clientFactory.newPayerClient(payer.getFhirServerUri(), payer);
    payerClient.registerInterceptor(requestInterceptor);
    if (token != null) {
      payerClient.registerInterceptor(new OAuthTokenInterceptor(fhirAuthService, token));
    }
    return payerClient;
  }
}
