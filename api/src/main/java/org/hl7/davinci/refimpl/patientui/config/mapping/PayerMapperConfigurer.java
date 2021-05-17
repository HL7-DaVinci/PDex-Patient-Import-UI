package org.hl7.davinci.refimpl.patientui.config.mapping;

import org.hl7.davinci.refimpl.patientui.dto.PayerDto;
import org.hl7.davinci.refimpl.patientui.model.Payer;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Configures the {@link Payer} related mappings.
 *
 * @author Kseniia Lutsko
 */
@Component
public class PayerMapperConfigurer implements ModelMapperConfigurer {

  private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  @Override
  public void configure(ModelMapper modelMapper) {
    Converter<OffsetDateTime, String> convertToString = ctx -> ctx.getSource() != null ? ctx.getSource()
        .atZoneSameInstant(ZoneOffset.UTC)
        .toLocalDateTime()
        .format(ISO_DATE_TIME) : null;

    modelMapper.typeMap(Payer.class, PayerDto.class)
        .addMappings(mapper -> mapper.using(convertToString)
            .map(Payer::getLastImported, PayerDto::setLastImported));

    modelMapper.typeMap(PayerDto.class, Payer.class)
        .addMappings(mapping -> mapping.skip(Payer::setLastImported))
        .addMappings(mapping -> mapping.skip(Payer::setSourcePatientId));
  }
}
