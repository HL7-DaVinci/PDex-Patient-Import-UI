package org.hl7.davinci.refimpl.patientui.fhir.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.impl.PayerClient;
import org.hl7.davinci.refimpl.patientui.model.Payer;

/**
 * A {@link ca.uhn.fhir.rest.client.api.IRestfulClientFactory} that can create the {@link PayerClient}.
 *
 * @author Taras Vuyiv
 */
public class PayerAwareClientFactory extends CustomRestfulClientFactory {

  public PayerAwareClientFactory(FhirContext theContext) {
    super(theContext);
  }

  /**
   * Creates a new {@link PayerClient} for the payer with the given ID.
   *
   * @param serverBase the server base
   * @param payer      the Payer
   * @return the {@link PayerClient}
   */
  public synchronized PayerClient newPayerClient(String serverBase, Payer payer) {
    validateConfigured();
    return new PayerClient(getFhirContext(), getHttpClient(serverBase), serverBase, this, payer);
  }
}
