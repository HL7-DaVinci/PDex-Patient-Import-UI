package org.hl7.davinci.refimpl.patientui.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.RuntimeSearchParam;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.davinci.refimpl.patientui.fhir.model.OAuthUris;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Extension;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Traverses the Capability Statement of the FHIR server extracting useful information, like security details, resources
 * support, etc.
 *
 * @author Taras Vuyiv
 */
@Slf4j
@Component
@RequiredArgsConstructor
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
    OAuthUris oAuthUris = new OAuthUris();
    if (capabilities != null) {
      CapabilityStatement.CapabilityStatementRestComponent rest = getServerRest(capabilities);
      if (rest != null && rest.getSecurity()
          .hasExtension(OAUTH_URIS_EXT)) {
        Extension oAuthExtension = rest.getSecurity()
            .getExtensionByUrl(OAUTH_URIS_EXT);
        oAuthUris.setAuthorize(oAuthExtension.getExtensionString(OAUTH_URIS_EXT_AUTHORIZE));
        oAuthUris.setToken(oAuthExtension.getExtensionString(OAUTH_URIS_EXT_TOKEN));
      }
    }
    return oAuthUris;
  }

  /**
   * Retrieves the capabilities from the given FHIR client, evaluates the given Patient search params mappings against
   * them and creates a map of the supported search parameters for each of the resource types.
   *
   * @param client         the FHIR client to get capabilities
   * @param searchMappings the map of resource types and the Patient search params
   * @return the supported parameters map
   */
  public Map<String, List<ReferenceClientParam>> getSupportedSearchParams(IGenericClient client,
      Map<String, List<RuntimeSearchParam>> searchMappings) {
    CapabilityStatement capabilities = retrieveCapabilities(client);
    Map<String, List<ReferenceClientParam>> resultMap = new ConcurrentHashMap<>();
    searchMappings.forEach((resourceType, searchParams) -> {
      List<ReferenceClientParam> supportedSearchParams = getSupportedSearchParams(capabilities, resourceType,
          searchParams);
      if (!supportedSearchParams.isEmpty()) {
        resultMap.put(resourceType, supportedSearchParams);
      }
    });
    return resultMap;
  }

  /**
   * Scans the given FHIR server capabilities and returns a list of found supported search parameters of the given
   * parameters list.
   *
   * @param capabilities the {@link CapabilityStatement} to examine
   * @param resourceType the type of resource to evaluate search parameters for
   * @param searchParams the search parameters to evaluate
   * @return list of supported search parameters
   */
  public List<ReferenceClientParam> getSupportedSearchParams(CapabilityStatement capabilities, String resourceType,
      Collection<RuntimeSearchParam> searchParams) {
    if (capabilities == null) {
      throw new IllegalStateException("The capability statement cannot be null.");
    }
    CapabilityStatement.CapabilityStatementRestResourceComponent resourceComponent = getResourceComponent(capabilities,
        resourceType);
    if (resourceComponent != null && isSearchSupported(resourceComponent)) {
      Set<String> usedPathLookup = new HashSet<>();
      return searchParams.stream()
          .filter(param -> !usedPathLookup.contains(param.getPath()))
          .filter(param -> resourceComponent.getSearchParam()
              .stream()
              .anyMatch(serverParam -> Objects.equals(serverParam.getName(), param.getName())))
          .map(param -> {
            usedPathLookup.add(param.getPath());
            return new ReferenceClientParam(param.getName());
          })
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private CapabilityStatement retrieveCapabilities(String fhirServerUri) {
    return retrieveCapabilities(fhirContext.newRestfulGenericClient(fhirServerUri));
  }

  private CapabilityStatement retrieveCapabilities(IGenericClient client) {
    CapabilityStatement capabilityStatement = null;
    try {
      capabilityStatement = client.capabilities()
          .ofType(CapabilityStatement.class)
          .execute();
    } catch (Exception e) {
      log.warn("Failed to retrieve the capability statement for {}: {}", client.getServerBase(), e);
    }
    return capabilityStatement;
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
