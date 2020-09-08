package org.hl7.davinci.refimpl.patientui.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@ConfigurationProperties(prefix = "app.payers")
@Configuration
@Component
@Data
public class Payers {

  private List<Payer> list;

  @Data
  public static class Payer {

    private String name;
    private URI authUri;
    private URI tokenUri;
    private URI fhirServerUri;
    private String clientId;
    private List<String> excludeResources;
    private String scope;
  }

}