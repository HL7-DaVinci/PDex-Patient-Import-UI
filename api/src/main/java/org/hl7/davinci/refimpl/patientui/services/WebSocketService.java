package org.hl7.davinci.refimpl.patientui.services;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.socket.RequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Handles sending the WebSocket messages to the subscribers.
 *
 * @author Taras Vuyiv
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WebSocketService {

  private static final String IMPORT_URL = "/import/{id}";

  private final SimpMessagingTemplate messageSender;

  /**
   * Notifies all the subscribers of the import topic about the ongoing request.
   *
   * @param payerId the ID of the Payer
   * @param payload the request details
   */
  public void notifyImport(Long payerId, RequestDto payload) {
    messageSender.convertAndSend(UriComponentsBuilder.fromUriString(IMPORT_URL)
        .buildAndExpand(payerId)
        .toUriString(), payload);
  }
}
