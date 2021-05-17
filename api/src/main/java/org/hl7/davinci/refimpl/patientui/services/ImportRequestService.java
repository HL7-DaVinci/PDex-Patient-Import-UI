package org.hl7.davinci.refimpl.patientui.services;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.ResponseBodyDto;
import org.hl7.davinci.refimpl.patientui.model.ImportRequest;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.hl7.davinci.refimpl.patientui.repository.ImportRequestRepository;
import org.hl7.davinci.refimpl.patientui.repository.PayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides the {@link ImportRequest} related functionality.
 *
 * @author Taras Vuyiv
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class ImportRequestService {

  private final PayerRepository payerRepository;
  private final ImportRequestRepository importRequestRepository;

  /**
   * Creates the given {@link ImportRequest} in the database.
   *
   * @param payerId       the ID of the Payer
   * @param importRequest the {@link ImportRequest} to create
   * @return the created {@link ImportRequest}
   */
  @Transactional
  public ImportRequest createImportRequest(Long payerId, ImportRequest importRequest) {
    Payer payer = payerRepository.getOne(payerId);
    importRequest.setPayer(payer);
    return importRequestRepository.save(importRequest);
  }

  /**
   * Creates the {@link ImportRequest} in the database.
   *
   * @param payerId      the ID of the Payer
   * @param requestId    the ID of request
   * @param responseBody the response body
   * @return the created {@link ImportRequest}
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ImportRequest createImportRequest(Long payerId, String requestId, String responseBody) {
    ImportRequest importRequest = new ImportRequest();
    importRequest.setRequestId(requestId);
    importRequest.setResponseBody(responseBody);
    return createImportRequest(payerId, importRequest);
  }

  /**
   * Returns the response body of the given request.
   *
   * @param payerId   the ID of the payer request was made for
   * @param requestId the ID of the request
   * @return {@link ResponseBodyDto}
   */
  public ResponseBodyDto getResponseBody(Long payerId, String requestId) {
    Payer payer = payerRepository.getOne(payerId);
    ImportRequest importRequest = importRequestRepository.findByPayerAndRequestId(payer, requestId)
        .orElseThrow(() -> new EmptyResultDataAccessException(
            String.format("No %s entity with payerId '%s' and requestId '%s' exists!",
                ImportRequest.class.getSimpleName(), payerId, requestId), 1));
    return new ResponseBodyDto(importRequest.getResponseBody());
  }

  /**
   * Removes all {@link ImportRequest}s from the database by the given {@link Payer}.
   *
   * @param payer the {@link Payer} to delete requests by
   */
  @Transactional
  public void deleteAllRequests(Payer payer) {
    importRequestRepository.deleteAllByPayer(payer);
  }
}
