package org.hl7.davinci.refimpl.patientui.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.fhir.importing.PatientReference;
import org.hl7.davinci.refimpl.patientui.fhir.model.OAuthUris;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Traverses the Capability Statement of the FHIR server extracting useful information, like security details, resources
 * support, etc.
 *
 * @author Taras Vuyiv
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FhirCapabilitiesLookup {

  private static final String OAUTH_URIS_EXT = "http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris";
  private static final String OAUTH_URIS_EXT_AUTHORIZE = "authorize";
  private static final String OAUTH_URIS_EXT_TOKEN = "token";

  private final FhirContext fhirContext;

  /**
   * Retrieves a {@link CapabilityStatement} from the FHIR server by the given URI, then searches in it for an OAuth URI
   * extension and extracts the authorization and token URIs.
   *
   * @param fhirServerUri the URI of the FHIR server to get auth data from
   * @return authorization and token OAuth URIs
   */
  public OAuthUris getOAuthUris(String fhirServerUri) {
    return getOAuthUris(retrieveCapabilities(fhirServerUri));
  }

  /**
   * Retrieves the OAuth URI extension from the Capability Statement of the given server and extracts the authorization
   * and token URIs.
   *
   * @param capabilities the {@link CapabilityStatement} to examine
   * @return authorization and token OAuth URIs
   */
  public OAuthUris getOAuthUris(CapabilityStatement capabilities) {
    CapabilityStatement.CapabilityStatementRestComponent rest = getServerRest(capabilities);
    OAuthUris oAuthUris = new OAuthUris();
    if (rest != null && rest.getSecurity()
        .hasExtension(OAUTH_URIS_EXT)) {
      Extension oAuthExtension = rest.getSecurity()
          .getExtensionByUrl(OAUTH_URIS_EXT);
      oAuthUris.setAuthorize(oAuthExtension.
          getExtensionString(OAUTH_URIS_EXT_AUTHORIZE));
      oAuthUris.setToken(oAuthExtension.
          getExtensionString(OAUTH_URIS_EXT_TOKEN));
    }
    return oAuthUris;
  }

  /**
   * Scans the given FHIR server capabilities and returns a list of found supported search parameters from the given
   * {@link PatientReference}.
   *
   * @param capabilities the {@link CapabilityStatement} to examine
   * @param mapping      the {@link PatientReference} to get params from
   * @return list of supported search parameters
   */
  public List<ReferenceClientParam> getSupportedSearchParams(CapabilityStatement capabilities,
      PatientReference mapping) {
    List<ReferenceClientParam> supportedParams = new ArrayList<>();
    CapabilityStatement.CapabilityStatementRestResourceComponent resourceComponent = getResourceComponent(capabilities,
        mapping.getResource()
            .getSimpleName());
    if (resourceComponent != null && isSearchSupported(resourceComponent)) {
      Predicate<ReferenceClientParam> supportFilter = clientParam -> resourceComponent.getSearchParam()
          .stream()
          .anyMatch(searchParam -> Objects.equals(searchParam.getName(), clientParam.getParamName()));
      mapping.getSiblingParams()
          .stream()
          .filter(supportFilter)
          .findFirst()
          .ifPresent(supportedParams::add);
      mapping.getSoloParams()
          .stream()
          .filter(supportFilter)
          .forEach(supportedParams::add);
    }
    return supportedParams;
  }

  private CapabilityStatement retrieveCapabilities(String fhirServerUri) {
    return fhirContext.newRestfulGenericClient(fhirServerUri)
        .capabilities()
        .ofType(CapabilityStatement.class)
        .execute();
  }

  private static boolean isSearchSupported(
      CapabilityStatement.CapabilityStatementRestResourceComponent resourceComponent) {
    return resourceComponent.getInteraction()
        .stream()
        .anyMatch(i -> i.getCode() == CapabilityStatement.TypeRestfulInteraction.SEARCHTYPE);
  }

  private static CapabilityStatement.CapabilityStatementRestResourceComponent getResourceComponent(
      CapabilityStatement capabilities, String resourceType) {
    CapabilityStatement.CapabilityStatementRestComponent rest = getServerRest(capabilities);
    return rest != null ? find(rest.getResource(), r -> Objects.equals(resourceType, r.getType())) : null;
  }

  private static CapabilityStatement.CapabilityStatementRestComponent getServerRest(CapabilityStatement capabilities) {
    return find(capabilities.getRest(), r -> r.getMode() == CapabilityStatement.RestfulCapabilityMode.SERVER);
  }

  private static <T> T find(Collection<T> collection, Predicate<? super T> predicate) {
    return collection.stream()
        .filter(predicate)
        .findFirst()
        .orElse(null);
  }
}
