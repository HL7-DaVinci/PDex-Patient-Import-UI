package org.hl7.davinci.refimpl.patientui.dto.socket;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Holds the HTTP request properties.
 *
 * @author Taras Vuyiv
 */
@Getter
@Setter
public class RequestDto {

  private String requestId;
  private RequestStatus requestStatus;
  private RequestType requestType;
  private String requestMethod;
  private String requestUri;
  private Map<String, List<String>> requestHeaders;
  private String requestBody;
  private Long requestDuration;
  private Integer responseStatus;
  private String responseStatusInfo;
  private Map<String, List<String>> responseHeaders;
  private String errorMessage;

  public RequestDto(RequestType requestType, RequestStatus requestStatus) {
    this(UUID.randomUUID()
        .toString(), requestType, requestStatus);
  }

  public RequestDto(String requestId, RequestType requestType, RequestStatus requestStatus) {
    this.requestId = requestId;
    this.requestStatus = requestStatus;
    this.requestType = requestType;
  }
}
