package org.hl7.davinci.refimpl.patientui.config.mapping;

import ca.uhn.fhir.rest.client.apache.ApacheHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import org.hl7.davinci.refimpl.patientui.dto.socket.RequestDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Configures the {@link IHttpRequest} related mappings.
 *
 * @author Taras Vuyiv
 */
@Component
public class HttpRequestMapperConfigurer implements ModelMapperConfigurer {

  @Override
  public void configure(ModelMapper modelMapper) {
    modelMapper.typeMap(ApacheHttpRequest.class, RequestDto.class)
        .addMapping(IHttpRequest::getUri, RequestDto::setRequestUri)
        .addMapping(IHttpRequest::getHttpVerbName, RequestDto::setRequestMethod)
        .addMapping(IHttpRequest::getAllHeaders, RequestDto::setRequestHeaders);
  }
}
