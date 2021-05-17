package org.hl7.davinci.refimpl.patientui.config.fhir;

import ca.uhn.fhir.context.FhirContext;
import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.config.fhir.dao.ExtendedDaoConfig;
import org.hl7.davinci.refimpl.patientui.fhir.CustomRestfulClientFactory;
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
    CustomRestfulClientFactory clientFactory = new CustomRestfulClientFactory(fhirContext);
    clientFactory.setSocketTimeout(60000);
    fhirContext.setRestfulClientFactory(clientFactory);
  }

  @Bean
  public ExtendedDaoConfig fhirDaoConfig() {
    return new ExtendedDaoConfig();
  }
}
