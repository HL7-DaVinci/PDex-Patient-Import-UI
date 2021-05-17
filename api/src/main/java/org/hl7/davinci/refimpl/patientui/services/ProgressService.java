package org.hl7.davinci.refimpl.patientui.services;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.dto.ProgressDto;
import org.hl7.davinci.refimpl.patientui.services.progress.Progress;
import org.hl7.davinci.refimpl.patientui.services.progress.ProgressManager;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides the {@link Progress} data of the Payer operations.
 *
 * @author Taras Vuyiv
 */
@Service
@RequiredArgsConstructor
public class ProgressService {

  private final ProgressManager progressManager;
  private final ModelMapper modelMapper;

  /**
   * Returns all the available progress records.
   *
   * @return the list of {@link ProgressDto}
   */
  public List<ProgressDto> getProgresses() {
    return progressManager.getAll()
        .stream()
        .map(progress -> modelMapper.map(progress, ProgressDto.class))
        .collect(Collectors.toList());
  }

  /**
   * Returns the operation progress by its ID.
   *
   * @param id the progress ID
   * @return the {@link ProgressDto}
   * @throws EmptyResultDataAccessException in case not progress by the given ID was found
   */
  public ProgressDto getProgress(Long id) {
    Progress progress = progressManager.get(id);
    if (progress == null) {
      throw new EmptyResultDataAccessException("A progress with ID '" + id + "' does not exist.", 1);
    }
    return modelMapper.map(progress, ProgressDto.class);
  }
}
