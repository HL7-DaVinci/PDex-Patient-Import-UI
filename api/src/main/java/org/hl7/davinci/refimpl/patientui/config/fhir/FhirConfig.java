package org.hl7.davinci.refimpl.patientui.config.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.config.fhir.dao.ExtendedDaoConfig;
import org.hl7.davinci.refimpl.patientui.fhir.client.PayerAwareClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FhirConfig {

  private final FhirContext fhirContext;

  @PostConstruct
  public void configureFhirContext() {
    IRestfulClientFactory clientFactory = new PayerAwareClientFactory(fhirContext);
    clientFactory.setServerValidationMode(ServerValidationModeEnum.NEVER);
    fhirContext.setRestfulClientFactory(clientFactory);
  }

  @Bean
  public ExtendedDaoConfig fhirDaoConfig() {
    return new ExtendedDaoConfig();
  }
}
