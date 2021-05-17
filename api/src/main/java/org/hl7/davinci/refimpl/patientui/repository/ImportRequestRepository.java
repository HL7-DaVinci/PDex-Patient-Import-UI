package org.hl7.davinci.refimpl.patientui.repository;

import org.hl7.davinci.refimpl.patientui.model.ImportRequest;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The {@link ImportRequest} JPA repository.
 *
 * @author Taras Vuyiv
 */
@Repository
public interface ImportRequestRepository extends JpaRepository<ImportRequest, Long> {

  Optional<ImportRequest> findByPayerAndRequestId(Payer payer, String requestId);

  void deleteAllByPayer(Payer payer);
}