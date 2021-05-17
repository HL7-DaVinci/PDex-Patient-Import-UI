package org.hl7.davinci.refimpl.patientui.fhir.interceptor;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.client.api.IRestfulClient;
import ca.uhn.fhir.rest.client.exceptions.FhirClientConnectionException;
import ca.uhn.fhir.rest.client.exceptions.FhirClientInappropriateForServerException;
import ca.uhn.fhir.rest.client.exceptions.InvalidResponseException;
import ca.uhn.fhir.rest.client.exceptions.NonFhirResponseException;
import ca.uhn.fhir.rest.client.impl.PayerClient;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hl7.davinci.refimpl.patientui.dto.socket.RequestDto;
import org.hl7.davinci.refimpl.patientui.dto.socket.RequestStatus;
import org.hl7.davinci.refimpl.patientui.dto.socket.RequestType;
import org.hl7.davinci.refimpl.patientui.services.ImportRequestService;
import org.hl7.davinci.refimpl.patientui.services.WebSocketService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * A HAPI FHIR interceptor that captures the HTTP request/response and sends its data to the socket topic based on the
 * ID of the payer.
 *
 * @author Taras Vuyiv
 */
@Log4j2
@Component
@Interceptor
@RequiredArgsConstructor
public class PayerRequestInterceptor {

  private final ImportRequestService importRequestService;
  private final WebSocketService socketService;
  private final ModelMapper modelMapper;

  @Hook(Pointcut.CLIENT_REQUEST)
  public void handleRequest(IHttpRequest request, IRestfulClient client) {
    RequestDto payload = new RequestDto(getRequestId(request), RequestType.FHIR, RequestStatus.PENDING);
    modelMapper.map(request, payload);
    socketService.notifyImport(getPayerId(client), payload);
  }

  @Hook(Pointcut.CLIENT_RESPONSE)
  public void handleResponse(IHttpRequest request, IHttpResponse response, IRestfulClient client) throws IOException {
    Long payerId = getPayerId(client);
    RequestDto payload = new RequestDto(getRequestId(request), RequestType.FHIR, RequestStatus.COMPLETED);
    modelMapper.map(request, payload);
    modelMapper.map(response, payload);
    persistRequest(payload, response, payerId);
    socketService.notifyImport(payerId, payload);
  }

  @Hook(Pointcut.SERVER_HANDLE_EXCEPTION)
  public boolean handleException(IHttpRequest request, IRestfulClient client, BaseServerResponseException exception) {
    log.warn(exception.getMessage());
    if (exception instanceof FhirClientConnectionException
        || exception instanceof FhirClientInappropriateForServerException
        || exception instanceof InvalidResponseException || exception instanceof NonFhirResponseException) {
      RequestDto payload = new RequestDto(getRequestId(request), RequestType.FHIR, RequestStatus.FAILED);
      payload.setErrorMessage(exception.getMessage());
      modelMapper.map(request, payload);
      socketService.notifyImport(getPayerId(client), payload);
    }
    return false;
  }

  private void persistRequest(RequestDto requestDto, IHttpResponse httpResponse, Long payerId) throws IOException {
    // Buffer the response content to enable further content processing by the HAPI client
    httpResponse.bufferEntity();
    String responseBody = new BufferedReader(
        new InputStreamReader(httpResponse.readEntity(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

    // If we ever going to support persisting the full requests - just map the missing fields from requestDto
    importRequestService.createImportRequest(payerId, requestDto.getRequestId(), responseBody);
  }

  private static String getRequestId(IHttpRequest request) {
    return request.getAllHeaders()
        .get(Constants.HEADER_REQUEST_ID)
        .get(0);
  }

  private static Long getPayerId(IRestfulClient client) {
    if (!(client instanceof PayerClient)) {
      throw new IllegalStateException("Cannot retrieve Payer ID. The client should be of type " + PayerClient.class);
    }
    return ((PayerClient) client).getPayer()
        .getId();
  }
}
