package org.hl7.davinci.refimpl.patientui.config.mapping;

import ca.uhn.fhir.rest.client.apache.ApacheHttpResponse;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import org.hl7.davinci.refimpl.patientui.dto.socket.RequestDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Configures the {@link IHttpResponse} related mappings.
 *
 * @author Taras Vuyiv
 */
@Component
public class HttpResponseMapperConfigurer implements ModelMapperConfigurer {

  @Override
  public void configure(ModelMapper modelMapper) {
    modelMapper.typeMap(ApacheHttpResponse.class, RequestDto.class)
        .addMapping(IHttpResponse::getAllHeaders, RequestDto::setResponseHeaders)
        .addMapping(IHttpResponse::getStatus, RequestDto::setResponseStatus)
        .addMapping(IHttpResponse::getStatusInfo, RequestDto::setResponseStatusInfo)
        .addMapping(response -> response.getRequestStopWatch()
            .getMillis(), RequestDto::setRequestDuration);
  }
}
