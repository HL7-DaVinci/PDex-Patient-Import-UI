package org.hl7.davinci.refimpl.patientui.config;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.config.mapping.ModelMapperConfigurer;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * The configuration of {@link ModelMapper}.
 *
 * @author Kseniia Lutsko
 */
@Configuration
@RequiredArgsConstructor
public class ModelMapperConfig {

  private final List<ModelMapperConfigurer> configurers;

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @PostConstruct
  public void configureMapper() {
    ModelMapper modelMapper = modelMapper();
    configurers.forEach(configurer -> configurer.configure(modelMapper));
  }
}
