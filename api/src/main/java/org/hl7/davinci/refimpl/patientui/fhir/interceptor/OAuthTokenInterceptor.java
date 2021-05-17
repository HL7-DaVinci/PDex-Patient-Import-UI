package org.hl7.davinci.refimpl.patientui.fhir.interceptor;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IRestfulClient;
import ca.uhn.fhir.rest.client.impl.PayerClient;
import org.hl7.davinci.refimpl.patientui.dto.authorization.OAuthTokenResponse;
import org.hl7.davinci.refimpl.patientui.services.FhirAuthService;

import java.time.OffsetDateTime;

/**
 * Intercepts the client request adding the Authorization header with the bearer access token. Closer to the end of the
 * token expiration time it will retrieve a new one using the refresh token if available.
 *
 * @author Taras Vuyiv
 */
@Interceptor
public class OAuthTokenInterceptor {

  private static final int REFRESH_MARGIN_SEC = 5;

  private final FhirAuthService fhirAuthService;

  private OAuthTokenResponse token;
  private volatile OffsetDateTime tokenExpireTime;

  public OAuthTokenInterceptor(FhirAuthService fhirAuthService, OAuthTokenResponse token) {
    this.fhirAuthService = fhirAuthService;
    setToken(token);
  }

  @Hook(Pointcut.CLIENT_REQUEST)
  public void interceptRequest(IHttpRequest request, IRestfulClient client) {
    OffsetDateTime now = OffsetDateTime.now();
    String refreshToken = token.getRefreshToken();
    if (refreshToken != null && now.isAfter(tokenExpireTime)) {
      synchronized (OAuthTokenInterceptor.class) {
        if (now.isAfter(tokenExpireTime)) {
          setToken(fhirAuthService.retrieveAccessTokenByRefreshToken(((PayerClient) client).getPayer(), refreshToken));
        }
      }
    }
    request.addHeader(Constants.HEADER_AUTHORIZATION,
        Constants.HEADER_AUTHORIZATION_VALPREFIX_BEARER + token.getAccessToken());
  }

  private void setToken(OAuthTokenResponse token) {
    this.token = token;
    this.tokenExpireTime = OffsetDateTime.now()
        .plusSeconds(token.getExpiresIn())
        .minusSeconds(REFRESH_MARGIN_SEC);
  }
}
