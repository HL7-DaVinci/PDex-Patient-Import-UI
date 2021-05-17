package org.hl7.davinci.refimpl.patientui.controllers;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.ProgressDto;
import org.hl7.davinci.refimpl.patientui.services.ProgressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Provides endpoints related to the progress of the Payer operations.
 *
 * @author Taras Vuyiv
 */
@RestController
@RequestMapping("api/progress")
@RequiredArgsConstructor
public class ProgressController {

  private final ProgressService progressService;

  /**
   * Returns all the available progress records.
   *
   * @return the list of {@link ProgressDto}
   */
  @GetMapping
  public List<ProgressDto> getProgresses() {
    return progressService.getProgresses();
  }

  /**
   * Returns the operation progress by its ID.
   *
   * @param id the progress ID
   * @return the {@link ProgressDto}
   */
  @GetMapping({"/{id}"})
  public ProgressDto getProgress(@PathVariable Long id) {
    return progressService.getProgress(id);
  }
}
