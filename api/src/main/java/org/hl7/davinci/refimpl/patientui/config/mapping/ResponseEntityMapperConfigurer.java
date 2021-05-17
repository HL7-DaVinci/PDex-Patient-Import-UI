package org.hl7.davinci.refimpl.patientui.config.mapping;

import org.hl7.davinci.refimpl.patientui.dto.socket.RequestDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Configures the {@link ResponseEntity} related mappings.
 *
 * @author Taras Vuyiv
 */
@Component
public class ResponseEntityMapperConfigurer implements ModelMapperConfigurer {

  @Override
  public void configure(ModelMapper modelMapper) {
    Converter<HttpStatus, String> statusInfoConverter = ctx -> ctx.getSource() != null ? ctx.getSource()
        .getReasonPhrase() : null;

    modelMapper.typeMap(ResponseEntity.class, RequestDto.class)
        .addMapping(ResponseEntity::getHeaders, RequestDto::setResponseHeaders)
        .addMapping(ResponseEntity::getStatusCodeValue, RequestDto::setResponseStatus)
        .addMappings(mapper -> mapper.using(statusInfoConverter)
            .map(ResponseEntity::getStatusCode, RequestDto::setResponseStatusInfo));
  }
}
