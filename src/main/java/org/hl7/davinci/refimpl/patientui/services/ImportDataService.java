package org.hl7.davinci.refimpl.patientui.services;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import lombok.Getter;
import org.hl7.davinci.pdex.refimpl.importer.ImportRequest;
import org.hl7.davinci.pdex.refimpl.importer.TargetConfiguration;
import org.hl7.davinci.pdex.refimpl.importer.simple.SimpleImporter;
import org.hl7.davinci.refimpl.patientui.config.properties.Payers;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;

@Service
public class ImportDataService {

  private Logger logger = LoggerFactory.getLogger(ImportDataService.class);

  @Value("${app.npi.system}")
  String npiSystem;
  @Value("${app.redirect-uri}")
  String redirectUri;
  @Autowired
  IParser parser;
  @Autowired
  FhirContext fhirContext;
  @Autowired
  Environment environment;

  public void importRecords(Payers.Payer payerConfig, String code) {
    TargetConfiguration targetConfiguration = new TargetConfiguration(parser, payerConfig.getExcludeResources(),
        npiSystem);
    IGenericClient receivedClient = FhirContext.forR4()
        .newRestfulGenericClient(payerConfig.getFhirServerUri()
            .toString());
    if (code == null) {
      throw new IllegalStateException("Code cannot be null. Authorized access to EHR is expected.");
    }
    Oath2Token token = getReceivedClientToken(payerConfig, code);
    IClientInterceptor is = new BearerTokenAuthInterceptor(token.getAccess_token());
    receivedClient.registerInterceptor(is);

    // Will not work with https. Schema needs to be externalized or database populated directly. In future there will
    // be no need in sample data since everything will be imported directly from Payer Sandboxes.
    IGenericClient targetClient = fhirContext.newRestfulGenericClient(
        "http://localhost:" + environment.getProperty("local.server.port") + "/fhir");
    // Since we are calling our own secured endpoint - credentials need to be provided. Do not use in production!
    targetClient.registerInterceptor(new BasicAuthInterceptor("user", "user"));

    // There should be a way to match a payer in our system with a remote. Also we should store credentials for
    // periodic data syncronization per payer. To be implemented.
    String system = "urn:uuid:" + UUID.randomUUID()
        .toString();

    //Create Organization in target system, map Patient to it and persist Patient
    //TODO update not supported for now, so make user target system is always different
    createTargetPayer(targetClient, payerConfig.getName(), system);
    Patient patient = createTargetPatient(receivedClient, token.getPatient(), targetClient, system);

    ImportRequest importRequest = new ImportRequest(receivedClient, system, "Patient/" + token.getPatient(),
        patient.getId(), targetClient);
    new SimpleImporter(targetConfiguration).importRecords(importRequest);
  }

  private Organization createTargetPayer(IGenericClient targetClient, String receivedName, String receivedSystem) {
    Organization org = new Organization().setName(receivedName);
    org.addType()
        .addCoding()
        .setSystem("http://terminology.hl7.org/CodeSystem/organization-type")
        .setCode("pay");
    org.addIdentifier()
        .setSystem(receivedSystem)
        .setValue(receivedName);
    targetClient.create()
        .resource(org)
        .execute();
    return org;
  }

  private Patient createTargetPatient(IGenericClient sourceClient, String patientId, IGenericClient targetClient,
      String system) {
    Patient sourcePatient = sourceClient.read()
        .resource(Patient.class)
        .withId(patientId)
        .execute();
    sourcePatient.addIdentifier()
        .setSystem(system)
        .setValue(sourcePatient.getId());
    //Set id as null to create a new one on persist.
    sourcePatient.setId((String) null);
    MethodOutcome execute = targetClient.create()
        .resource(sourcePatient)
        .execute();
    return (Patient) execute.getResource();
  }

  private Oath2Token getReceivedClientToken(Payers.Payer payerConfig, String code) {
    //Set Form Data
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "authorization_code");
    map.add("redirect_uri", redirectUri);
    map.add("client_id", payerConfig.getClientId());
    map.add("code", code);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

    return new RestTemplateBuilder().build()
        .postForEntity(payerConfig.getTokenUri(), request, Oath2Token.class)
        .getBody();
  }

  @Getter
  private static class Oath2Token {

    String access_token;
    String patient;
    String token_type;
    String expires_in;
    String refresh_token;
    String scope;
    String id_token;
  }
}
