package org.hl7.davinci.refimpl.patientui.importer;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
public class SampleDataImporter {

  @Autowired
  FhirContext fhirContext;
  @Autowired
  Environment environment;

  @Autowired
  private ResourceLoader resourceLoader;

  public void importData(String payer) throws SampleDataLoadException {
    // Will not work with https. Schema needs to be externalized or database populated directly. In future there will
    // be no need in sample data since everything will be imported directly from Payer Sandboxes.
    IGenericClient client = fhirContext.newRestfulGenericClient(
        "http://localhost:" + environment.getProperty("local.server.port") + "/fhir");
    // Since we are calling our own secured endpoint - credentials need to be provided. Do not use in production!
    client.registerInterceptor(new BasicAuthInterceptor("user", "user"));

    // There should be a way to match a payer in our system with a remote. Also we should store credentials for
    // periodic data syncronization per payer. To be implemented.
    String system = "urn:uuid:" + UUID.randomUUID()
        .toString();
    try (InputStream is = resourceLoader.getResource("classpath:importdata.json")
        .getInputStream()) {
      //We know a structure of a sample data and can retrieve records by sequence number.
      Bundle bundle = fhirContext.newJsonParser()
          .parseResource(Bundle.class, is);
      ((Organization) (bundle.getEntry()
          .get(0)
          .getResource())).setName(payer)
          .getIdentifierFirstRep()
          .setSystem(system);
      ((Patient) (bundle.getEntry()
          .get(1)
          .getResource())).getIdentifierFirstRep()
          .setSystem(system);
      client.transaction()
          .withBundle(bundle)
          .execute();
    } catch (IOException e) {
      throw new SampleDataLoadException(e);
    }
  }

  public static class SampleDataLoadException extends Exception {

    public SampleDataLoadException(Throwable cause) {
      super(cause);
    }
  }
}
