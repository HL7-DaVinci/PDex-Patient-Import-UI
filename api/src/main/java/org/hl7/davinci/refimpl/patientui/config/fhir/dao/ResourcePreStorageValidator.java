package org.hl7.davinci.refimpl.patientui.config.fhir.dao;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.dao.BaseHapiFhirResourceDao;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import ca.uhn.fhir.util.BundleUtil;
import ca.uhn.fhir.util.FhirTerser;
import ca.uhn.fhir.util.ResourceReferenceInfo;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * The FHIR resource validator. It is used to override the logic of the
 * {@link ca.uhn.fhir.jpa.dao.BaseStorageDao#preProcessResourceForStorage(IBaseResource)}
 * method. Unfortunately we cannot simply modify one of the validation checks (for example, here we only need to change
 * the behavior of the resource ID validation), because all of them are packed in the single method. So we copied all
 * the assertions from the {@link ca.uhn.fhir.jpa.dao.BaseStorageDao} in here.
 *
 * @author Taras Vuyiv
 */
public class ResourcePreStorageValidator {

  /**
   * Validates the resources prior to storage on the FHIR server.
   *
   * @param resource    the resource that is about to be stored
   * @param resourceDao the DAO of for the resource type
   * @param <T>         the type of the resource
   */
  public <T extends IBaseResource> void validate(T resource, BaseHapiFhirResourceDao<T> resourceDao) {
    DaoConfig daoConfig = resourceDao.getConfig();
    FhirContext fhirContext = resourceDao.getContext();
    String resourceName = resourceDao.getResourceName();

    String resourceType = fhirContext.getResourceType(resource);
    if (resourceName != null && !resourceName.equals(resourceType)) {
      throw new InvalidRequestException(fhirContext.getLocalizer()
          .getMessageSanitized(BaseHapiFhirResourceDao.class, "incorrectResourceType", resourceType, resourceName));
    }

    if (!(daoConfig instanceof ExtendedDaoConfig) || ((ExtendedDaoConfig) daoConfig).isValidateResourceIdOnPersist()) {
      validateResourceId(resource.getIdElement(), fhirContext);
    }

    Set<String> treatBaseUrlsAsLocal = daoConfig.getTreatBaseUrlsAsLocal();
    if (!treatBaseUrlsAsLocal.isEmpty()) {
      replaceAbsoluteReferences(resource, treatBaseUrlsAsLocal, fhirContext);
    }

    if ("Bundle".equals(resourceType)) {
      validateBundleType((IBaseBundle) resource, daoConfig.getBundleTypesAllowedForStorage(), fhirContext);
    }
  }

  private void validateResourceId(IIdType idElement, FhirContext context) {
    if (idElement.hasIdPart() && !idElement.isIdPartValid()) {
      throw new InvalidRequestException(context.getLocalizer()
          .getMessageSanitized(BaseHapiFhirResourceDao.class, "failedToCreateWithInvalidId", idElement.getIdPart()));
    }
  }

  private void validateBundleType(IBaseBundle bundle, Set<String> allowedTypes, FhirContext context) {
    String bundleType = BundleUtil.getBundleType(context, bundle);
    bundleType = defaultString(bundleType);
    if (!allowedTypes.contains(bundleType)) {
      String message = "Unable to store a Bundle resource on this server with a Bundle.type value of: " + (isNotBlank(
          bundleType) ? bundleType : "(missing)");
      throw new UnprocessableEntityException(message);
    }
  }

  private void replaceAbsoluteReferences(IBaseResource resource, Set<String> baseUrlsToReplace, FhirContext context) {
    FhirTerser terser = context.newTerser();
    List<ResourceReferenceInfo> refs = terser.getAllResourceReferences(resource);
    for (ResourceReferenceInfo nextRef : refs) {
      IIdType refId = nextRef.getResourceReference()
          .getReferenceElement();
      if (refId != null && refId.hasBaseUrl() && baseUrlsToReplace.contains(refId.getBaseUrl())) {
        IIdType newRefId = refId.toUnqualified();
        nextRef.getResourceReference()
            .setReference(newRefId.getValue());
      }
    }
  }
}
