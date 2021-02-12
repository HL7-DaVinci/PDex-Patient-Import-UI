package org.hl7.davinci.refimpl.patientui.fhir.importing;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides the ability to import the create a FHIR resource or a bundle of resources in a target FHIR server handling
 * the resource conversion (IDs, source system, etc.) before.
 *
 * @author Taras Vuyiv
 */
@Component
public class ResourceImporter {

  /**
   * Creates/updates a single resource in the target FHIR server. The old ID will be preserved, but will be prepended by
   * the payerId prefix (as all the references as well).
   *
   * @param targetClient the {@link IGenericClient} of the target server
   * @param resource     the resource to import
   * @param payerId      the local ID of the payer the resource came from
   * @return the {@link MethodOutcome} of the resource create/update
   */
  public MethodOutcome importResource(IGenericClient targetClient, Resource resource, Long payerId) {
    return targetClient.update()
        .resource(convert(resource, payerId))
        .execute();
  }

  /**
   * Retrieve a resource of the given type and with the given ID from the source server and imports it to the target
   * server.
   *
   * @param endpoints    the {@link IGenericClient}s of the source and target import servers
   * @param resourceType the type of resource to import
   * @param resourceId   the ID of resource to import
   * @param payerId      the local ID of the payer the resource came from
   * @return the {@link MethodOutcome} of the resource create/update
   */
  public MethodOutcome importResource(ImportEndpoints endpoints, Class<? extends Resource> resourceType,
      String resourceId, Long payerId) {
    Resource resource = endpoints.getSourceClient()
        .read()
        .resource(resourceType)
        .withId(resourceId)
        .execute();
    return importResource(endpoints.getTargetClient(), resource, payerId);
  }

  /**
   * Imports all resources the given bundle contains in the target FHIR server scrolling through the all bundle pages.
   * The old IDs will be preserved, but will be prepended by the payerId prefix (as all the references as well).
   *
   * @param endpoints the {@link IGenericClient}s of the source and target import servers
   * @param bundle    the bundle to import
   * @param payerId   the local ID of the payer the resource came from
   * @return the list of {@link MethodOutcome}s of the resources create/update
   */
  public List<MethodOutcome> importBundle(ImportEndpoints endpoints, Bundle bundle, Long payerId) {
    List<MethodOutcome> importOutcomes = bundle.getEntry()
        .stream()
        .map(Bundle.BundleEntryComponent::getResource)
        .map(resource -> importResource(endpoints.getTargetClient(), resource, payerId))
        .collect(Collectors.toList());

    if (bundle.getLink(IBaseBundle.LINK_NEXT) != null) {
      Bundle nextPage = endpoints.getSourceClient()
          .loadPage()
          .next(bundle)
          .execute();

      importOutcomes.addAll(importBundle(endpoints, nextPage, payerId));
    }
    return importOutcomes;
  }

  protected <T extends Resource> T convert(T resource, Long payerId) {
    // Link a resource to the payer setting its ID to the 'source' field:
    resource.getMeta()
        .setSource(payerId.toString());

    // Updating the ID of resource prepending ID of the payer:
    resource.setId(composeId(resource.getIdElement()
        .getIdPart(), payerId));
    return resource;
  }

  protected String composeId(String originalId, Long payerId) {
    return payerId + "-" + originalId;
  }
}
