package org.hl7.davinci.refimpl.patientui.fhir.importing.executor;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.davinci.refimpl.patientui.fhir.importing.ImportEndpoints;
import org.hl7.davinci.refimpl.patientui.model.Payer;

import java.util.List;

/**
 * A common interface for the Patient data import executors.
 *
 * @author Taras Vuyiv
 */
public interface ImportExecutor {

  /**
   * Executes the logic for importing the Patient data from source server to target server based on the given Payer.
   *
   * @param endpoints the {@link IGenericClient}s of the source and target import servers
   * @param payer     the {@link Payer} associated with the import
   * @return a {@link MethodOutcome} list of the target server import calls
   */
  List<MethodOutcome> doImport(ImportEndpoints endpoints, Payer payer);

  /**
   * Executes the logic for refreshing the Patient data from source server to target server based on the given Payer.
   *
   * @param endpoints the {@link IGenericClient}s of the source and target import servers
   * @param payer     the {@link Payer} associated with the refresh
   * @return a {@link MethodOutcome} list of the target server import calls
   */
  List<MethodOutcome> doRefresh(ImportEndpoints endpoints, Payer payer);
}
