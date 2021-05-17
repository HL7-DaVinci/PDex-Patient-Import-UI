package org.hl7.davinci.refimpl.patientui.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.apache.ApacheRestfulClientFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import java.util.concurrent.TimeUnit;

/**
 * The {@link ApacheRestfulClientFactory} with the custom configuration.
 *
 * @author Taras Vuyiv
 */
public class CustomRestfulClientFactory extends ApacheRestfulClientFactory {

  private HttpClient httpClient;

  public CustomRestfulClientFactory(FhirContext fhirContext) {
    super(fhirContext);
  }

  @Override
  public HttpClient getNativeHttpClient() {
    if (httpClient == null) {
      Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http",
          PlainConnectionSocketFactory.getSocketFactory())

          // As this is a demo-purpose application, to simplify the configuration we create a SSL connection factory
          // which trusts all certificates, including self-signed ones:
          .register("https", createSSLConnectionFactory())
          .build();

      // The rest of this method is just copied from super:
      PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry, null,
          null, null, 5000, TimeUnit.MILLISECONDS);
      connectionManager.setMaxTotal(getPoolMaxTotal());
      connectionManager.setDefaultMaxPerRoute(getPoolMaxPerRoute());

      RequestConfig defaultRequestConfig = RequestConfig.custom()
          .setSocketTimeout(getSocketTimeout())
          .setConnectTimeout(getConnectTimeout())
          .setConnectionRequestTimeout(getConnectionRequestTimeout())
          .setStaleConnectionCheckEnabled(true)
          .build();

      httpClient = HttpClients.custom()
          .setConnectionManager(connectionManager)
          .setDefaultRequestConfig(defaultRequestConfig)
          .disableCookieManagement()
          .build();
    }
    return httpClient;
  }

  @Override
  protected void resetHttpClient() {
    this.httpClient = null;
  }

  @Override
  public synchronized void setHttpClient(Object httpClient) {
    this.httpClient = (HttpClient) httpClient;
  }

  @Override
  public void setProxy(String theHost, Integer thePort) {
    throw new UnsupportedOperationException();
  }

  private static SSLConnectionSocketFactory createSSLConnectionFactory() {
    try {
      return new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
          .build(), NoopHostnameVerifier.INSTANCE);
    } catch (Exception e) {
      throw new SSLFactoryInitException(e);
    }
  }

  private static class SSLFactoryInitException extends RuntimeException {

    private SSLFactoryInitException(Throwable cause) {
      super(cause);
    }
  }
}
