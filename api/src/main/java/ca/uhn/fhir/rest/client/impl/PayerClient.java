package ca.uhn.fhir.rest.client.impl;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.interceptor.api.HookParams;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.client.api.IHttpClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IRestfulClient;
import ca.uhn.fhir.rest.client.method.IClientResponseHandler;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import lombok.Getter;
import org.hl7.davinci.refimpl.patientui.model.Payer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The {@link GenericClient} extension that knows for which specific
 * {@link org.hl7.davinci.refimpl.patientui.model.Payer}
 * it will be used. Using an out-of-project HAPI package in order to be able to fix a poor extensibility of the client.
 *
 * @author Taras Vuyiv
 */
public class PayerClient extends GenericClient {

  @Getter
  private final Payer payer;

  public PayerClient(FhirContext theContext, IHttpClient theHttpClient, String theServerBase,
      RestfulClientFactory theFactory, Payer payer) {
    super(theContext, theHttpClient, theServerBase, theFactory);
    this.payer = payer;
  }

  @Override
  <T> T invokeClient(FhirContext theContext, IClientResponseHandler<T> binding,
      BaseHttpClientInvocation clientInvocation, EncodingEnum theEncoding, Boolean thePrettyPrint,
      boolean theLogRequestAndResponse, SummaryEnum theSummaryMode, Set<String> theSubsetElements,
      CacheControlDirective theCacheControlDirective, String theCustomAcceptHeader,
      Map<String, List<String>> theCustomHeaders) {
    clientInvocation.addHeader(Constants.HEADER_REQUEST_ID, UUID.randomUUID()
        .toString());
    try {
      return super.invokeClient(theContext, binding, clientInvocation, theEncoding, thePrettyPrint,
          theLogRequestAndResponse, theSummaryMode, theSubsetElements, theCacheControlDirective, theCustomAcceptHeader,
          theCustomHeaders);
    } catch (BaseServerResponseException exception) {
      HookParams hookParams = new HookParams();
      hookParams.add(IHttpRequest.class,
          clientInvocation.asHttpRequest(getServerBase(), Collections.emptyMap(), getEncoding(), thePrettyPrint));
      hookParams.add(IRestfulClient.class, this);
      hookParams.add(BaseServerResponseException.class, exception);
      // Although this is technically a client exception, not a server one, we use the server Pointcut anyway,
      // because it is just not possible to add a new hook type to the enum:
      if (getInterceptorService().callHooks(Pointcut.SERVER_HANDLE_EXCEPTION, hookParams)) {
        throw exception;
      }
    }
    // In case an exception was handled by a hook, we simply return null. The client users should handle it by
    // themselves
    return null;
  }
}
