package org.hl7.davinci.refimpl.patientui.services;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.hl7.davinci.refimpl.patientui.dto.PayerDto;
import org.hl7.davinci.refimpl.patientui.dto.authorization.OAuthTokenResponse;
import org.hl7.davinci.refimpl.patientui.dto.socket.RequestDto;
import org.hl7.davinci.refimpl.patientui.dto.socket.RequestStatus;
import org.hl7.davinci.refimpl.patientui.dto.socket.RequestType;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.hl7.davinci.refimpl.patientui.services.exception.FhirAuthorizationException;
import org.hl7.fhir.r4.model.IdType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Provides FHIR server authorization capabilities.
 *
 * @author Taras Vuyiv
 */
@Service
@RequiredArgsConstructor
public class FhirAuthService {

  private final ImportRequestService importRequestService;
  private final WebSocketService socketService;

  private final ObjectMapper objectMapper;
  private final ModelMapper modelMapper;

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${app.fhir.oauth.redirectUri}")
  private String authRedirectUri;

  /**
   * Builds the FHIR OAuth authorization URL for the given Payer. It should navigate to the Payer's identity provider to
   * receive the authorization code for the FHIR server token access.
   *
   * @param payer the Payer a Patient is going to be authorized by
   * @return authorization URL
   */
  public String authorizePatientUrl(PayerDto payer, String state) {
    try {
      return new URIBuilder(payer.getAuthorizeUri()).addParameter("client_id", payer.getClientId())
          .addParameter("aud", payer.getFhirServerUri())
          .addParameter("scope", payer.getScope())
          .addParameter("redirect_uri", authRedirectUri)
          .addParameter("state", state)
          .addParameter("response_type", "code")
          .build()
          .toString();
    } catch (URISyntaxException e) {
      throw new FhirAuthorizationException("Failed to build the FHIR OAuth authorization URL.", e);
    }
  }

  /**
   * Notifies the the import socket topic of the Payer about the authorization request.
   *
   * @param payerId      the ID of the Payer authorization is performed
   * @param authorizeUrl the authorization URL
   */
  public void notifyPatientAuthorization(Long payerId, String authorizeUrl) {
    RequestDto requestDto = new RequestDto(RequestType.AUTH, RequestStatus.UNDEFINED);
    requestDto.setRequestUri(authorizeUrl);
    requestDto.setRequestMethod(HttpMethod.GET.name());
    socketService.notifyImport(payerId, requestDto);
  }

  /**
   * Retrieves an OAuth token from the Payer's authorization server based on the given access code.
   *
   * @param payer    the {@link Payer}
   * @param authCode the authorization code
   * @return the {@link OAuthTokenResponse}
   * @throws FhirAuthorizationException in case the token retrieval failed
   */
  public OAuthTokenResponse retrieveAccessTokenByCode(Payer payer, String authCode) {
    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add("grant_type", "authorization_code");
    requestBody.add("code", authCode);
    requestBody.add("client_id", payer.getClientId());
    requestBody.add("redirect_uri", authRedirectUri);
    return postTokenRequest(payer, requestBody);
  }

  /**
   * Retrieves an OAuth token from the Payer's authorization server based on the given refresh token.
   *
   * @param payer        the {@link Payer}
   * @param refreshToken the refresh token
   * @return the {@link OAuthTokenResponse}
   * @throws FhirAuthorizationException in case the token retrieval failed
   */
  public OAuthTokenResponse retrieveAccessTokenByRefreshToken(Payer payer, String refreshToken) {
    MultiValueMap<String, String> formDataBody = new LinkedMultiValueMap<>();
    formDataBody.add("grant_type", "refresh_token");
    formDataBody.add("refresh_token", refreshToken);
    formDataBody.add("client_id", payer.getClientId());
    return postTokenRequest(payer, formDataBody);
  }

  /**
   * Extracts the Patient ID from the FHIR OAuth {@link OAuthTokenResponse}.
   *
   * @param tokenResponse the token response
   * @return the ID of the Patient
   */
  public String extractPatient(String serverUri, OAuthTokenResponse tokenResponse) {
    String patient = null;
    // A special logic for Logica Health as it does not support user scope yet:
    if ("api.logicahealth.org".equals(URI.create(serverUri)
        .getHost())) {
      patient = tokenResponse.getPatient();
    } else {
      String idToken = tokenResponse.getIdToken();
      String fhirUser = idToken != null ? JWT.decode(idToken)
          .getClaim("fhirUser")
          .as(String.class) : null;
      if (fhirUser != null) {
        IdType fhirUserId = new IdType(fhirUser);
        if ("Patient".equals(fhirUserId.getResourceType())) {
          patient = fhirUserId.getIdPart();
        }
      }
    }
    if (patient == null) {
      throw new FhirAuthorizationException("Failed to extract Patient ID from the OAuth token.");
    }
    return patient;
  }

  private OAuthTokenResponse postTokenRequest(Payer payer, MultiValueMap<String, String> requestBody) {
    String tokenUri = payer.getTokenUri();
    if (StringUtils.isBlank(tokenUri)) {
      throw new FhirAuthorizationException("Failed to fetch token. The Payer does not support OAuth.");
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    RequestDto requestDto = createTokenRequestDto(tokenUri, headers, requestBody);

    Long payerId = payer.getId();

    socketService.notifyImport(payerId, requestDto);

    StopWatch stopWatch = new StopWatch();
    try {
      stopWatch.start();
      ResponseEntity<OAuthTokenResponse> tokenEntity = restTemplate.postForEntity(tokenUri,
          new HttpEntity<>(requestBody, headers), OAuthTokenResponse.class);
      stopWatch.stop();
      OAuthTokenResponse tokenResponseBody = tokenEntity.getBody();
      modelMapper.map(tokenEntity, requestDto);
      requestDto.setRequestDuration(stopWatch.getTotalTimeMillis());
      notifyTokenResponse(payerId, requestDto, asJson(tokenResponseBody));
      return tokenResponseBody;
    } catch (RestClientResponseException e) {
      stopWatch.stop();
      requestDto.setRequestDuration(stopWatch.getTotalTimeMillis());
      requestDto.setResponseHeaders(e.getResponseHeaders());
      requestDto.setResponseStatus(e.getRawStatusCode());
      requestDto.setResponseStatusInfo(e.getStatusText());
      notifyTokenResponse(payerId, requestDto, e.getResponseBodyAsString());
      throw new FhirAuthorizationException("The OAuth retrieve access token request failed.", e);
    }
  }

  private RequestDto createTokenRequestDto(String requestUri, HttpHeaders headers, Object requestBody) {
    RequestDto requestDto = new RequestDto(RequestType.AUTH, RequestStatus.PENDING);
    requestDto.setRequestUri(requestUri);
    requestDto.setRequestMethod(HttpMethod.POST.name());
    requestDto.setRequestHeaders(headers);
    requestDto.setRequestBody(asJson(requestBody));
    return requestDto;
  }

  private void notifyTokenResponse(Long payerId, RequestDto requestDto, String responseBody) {
    requestDto.setRequestStatus(RequestStatus.COMPLETED);
    importRequestService.createImportRequest(payerId, requestDto.getRequestId(), responseBody);
    socketService.notifyImport(payerId, requestDto);
  }

  private String asJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new FhirAuthorizationException("An object to JSON conversion failed.", e);
    }
  }
}
