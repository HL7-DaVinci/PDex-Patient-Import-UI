package org.hl7.davinci.refimpl.patientui.dto.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * A FHIR OAuth token response DTO.
 *
 * @author Taras Vuyiv
 */
@Getter
@Setter
public class OAuthTokenResponse {

  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("token_type")
  private String tokenType;
  @JsonProperty("expires_in")
  private Integer expiresIn;
  @JsonProperty("refresh_token")
  private String refreshToken;
  @JsonProperty("id_token")
  private String idToken;
  private String scope;
  private String patient;
}
