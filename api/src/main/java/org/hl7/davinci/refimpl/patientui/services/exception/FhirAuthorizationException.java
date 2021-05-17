package org.hl7.davinci.refimpl.patientui.services.exception;

/**
 * Occurs during an attempt to authorize the Patient at the Payer server.
 *
 * @author Taras Vuyiv
 */
public class FhirAuthorizationException extends RuntimeException {

  private static final long serialVersionUID = -4155184118873005454L;

  public FhirAuthorizationException(String message) {
    super(message);
  }

  public FhirAuthorizationException(String message, Throwable cause) {
    super(message, cause);
  }
}
