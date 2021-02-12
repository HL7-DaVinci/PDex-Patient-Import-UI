package org.hl7.davinci.refimpl.patientui.config.fhir;

import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.provider.r4.JpaSystemProviderR4;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.spring.boot.autoconfigure.FhirRestfulServerCustomizer;
import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.config.fhir.dao.ExtendedDaoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FhirRestfulServerConfig implements FhirRestfulServerCustomizer {

  private final JpaSystemProviderR4 mySystemProviderR4;
  private final ExtendedDaoConfig daoConfig;

  @Override
  //Support Transaction Bundles, Cascading deletes and other capabilities.
  public void customize(RestfulServer restfulServer) {
    restfulServer.registerProvider(mySystemProviderR4);
    daoConfig.setAllowExternalReferences(true);
    daoConfig.setAllowMultipleDelete(true);
    daoConfig.setEnforceReferentialIntegrityOnWrite(false);
    daoConfig.setEnforceReferentialIntegrityOnDelete(false);
    daoConfig.setResourceClientIdStrategy(DaoConfig.ClientIdStrategyEnum.ANY);
    daoConfig.setIndexMissingFields(DaoConfig.IndexEnabledEnum.ENABLED);
    daoConfig.setValidateResourceIdOnPersist(false);
  }
}