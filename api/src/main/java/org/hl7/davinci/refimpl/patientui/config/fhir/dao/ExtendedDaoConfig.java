package org.hl7.davinci.refimpl.patientui.config.fhir.dao;

import ca.uhn.fhir.jpa.api.config.DaoConfig;

/**
 * Provides additional custom configuration options to the {@link DaoConfig}.
 *
 * @author Taras Vuyiv
 */
public class ExtendedDaoConfig extends DaoConfig {

  private boolean validateResourceIdOnPersist = true;

  /**
   * @return <code>true</code> if resource IDs are going to be validated before persist with an Update
   */
  public boolean isValidateResourceIdOnPersist() {
    return validateResourceIdOnPersist;
  }

  /**
   * If set to <code>false</code> (default is <code>true</code>) the resource ID will not be validated before creating
   * it on the FHIR server with an Update.
   *
   * @param validateResourceIdOnPersist the boolean value
   */
  public void setValidateResourceIdOnPersist(boolean validateResourceIdOnPersist) {
    this.validateResourceIdOnPersist = validateResourceIdOnPersist;
  }
}
