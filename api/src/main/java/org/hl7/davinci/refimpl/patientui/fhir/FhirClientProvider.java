package org.hl7.davinci.refimpl.patientui.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;

  /**
   * Create a FHIR client working with the internally running FHIR server.
   *
   * @return the {@link IGenericClient}
   */
  public IGenericClient newLocalClient() {
    // TODO: Schema needs to be externalized or database populated directly.
    // In future there will be no need in sample data since everything will be imported directly from Payer Sandboxes.
    String localClientUri = "https://localhost:" + environment.getProperty("local.server.port") + "/fhir";
    // Since we are calling our own secured endpoint - a security tokenResponse needs to be provided.
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken("user", "user"));
    return newSecuredClient(localClientUri, jwtUtils.generateJwtToken(authentication));
  }

  /**
   * Creates a FHIR client working with the FHIR server with the given URI.
   *
   * @param fhirServerUri the URI of the FHIR server
   * @param accessToken   the Bearer access token. If null, just a simple client will be created
   * @return the {@link IGenericClient}
   */
  public IGenericClient newSecuredClient(String fhirServerUri, String accessToken) {
    IGenericClient externalClient = fhirContext.newRestfulGenericClient(fhirServerUri);
    if (accessToken != null) {
      externalClient.registerInterceptor(new BearerTokenAuthInterceptor(accessToken));
    }
    return externalClient;
  }
}
