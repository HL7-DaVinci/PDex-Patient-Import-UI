package org.hl7.davinci.refimpl.patientui.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.interceptor.api.IInterceptorBroadcaster;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.interceptor.CascadingDeleteInterceptor;
import ca.uhn.fhir.jpa.provider.r4.JpaSystemProviderR4;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.spring.boot.autoconfigure.FhirRestfulServerCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
class RestfulServerConfig implements FhirRestfulServerCustomizer {

  @Autowired
  JpaSystemProviderR4 mySystemProviderR4;

  @Autowired
  FhirContext fhirContext;

  @Autowired
  DaoRegistry daoRegistry;

  @Autowired
  IInterceptorBroadcaster interceptorBroadcaster;

  @Autowired
  DaoConfig daoConfig;

  @Override
  //Support Transaction Bundles, Cascading deletes and other capabilities.
  public void customize(final RestfulServer restfulServer) {
    restfulServer.registerProvider(mySystemProviderR4);
    restfulServer.registerInterceptor(new CascadingDeleteInterceptor(fhirContext, daoRegistry, interceptorBroadcaster));
    daoConfig.setAllowExternalReferences(true);
    daoConfig.setEnforceReferentialIntegrityOnWrite(false);
    //daoConfig.setAllowInlineMatchUrlReferences(true);
    //restfulServer.registerInterceptor(new JWTAuthorizationInterceptor());
  }
}