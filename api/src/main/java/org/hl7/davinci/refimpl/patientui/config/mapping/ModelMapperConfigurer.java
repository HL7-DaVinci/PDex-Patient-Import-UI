package org.hl7.davinci.refimpl.patientui.config.mapping;

import org.modelmapper.ModelMapper;

/**
 * The concrete classes could extend the {@link ModelMapper} configuration with the specific mappings.
 *
 * @author Kseniia Lutsko
 */
public interface ModelMapperConfigurer {

  /**
   * Implement the method to configure the given mapper.
   *
   * @param modelMapper the {@link ModelMapper} to configure
   */
  void configure(ModelMapper modelMapper);
}
