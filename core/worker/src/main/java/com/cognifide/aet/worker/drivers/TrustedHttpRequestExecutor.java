/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.worker.drivers;

import com.cognifide.aet.job.api.collector.HttpRequestExecutor;
import com.cognifide.aet.job.api.collector.ResponseObject;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.SSLContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


public class TrustedHttpRequestExecutor implements HttpRequestExecutor {

  private static final String COOKIE = "Cookie";

  private final Map<String, String> headers = new LinkedHashMap<>();
  private final Map<String, String> cookies = new HashMap<>();

  @Override
  public ResponseObject executeGetRequest(String url, int timeoutInMs) throws IOException {
    final HttpGet request = new HttpGet(url);
    setHeaders(request);
    setCookies(request);

    try (CloseableHttpClient httpClient =
        HttpClients.custom()
            .setHostnameVerifier(new AllowAllHostnameVerifier())
            .setSslcontext(trustAllSslContext())
            .setDefaultRequestConfig(buildRequestConfig(timeoutInMs))
            .build();
        CloseableHttpResponse response = httpClient.execute(request)) {
      Header[] headersArray = response.getAllHeaders();
      Map<String, List<String>> tmpHeaders = new HashMap<>();
      for (Header header : headersArray) {
        tmpHeaders.put(header.getName(), Collections.singletonList(header.getValue()));
      }
      return new ResponseObject(tmpHeaders, IOUtils.toByteArray(response.getEntity().getContent()));
    } catch (Exception e) {
      throw new IOException(String.format("Failed to connect to %s - %s", url, e.getMessage()), e);
    }
  }

  @Override
  public TrustedHttpRequestExecutor addHeader(String key, String value) {
    headers.put(key, value);
    return this;
  }

  @Override
  public TrustedHttpRequestExecutor addCookie(String key, String value) {
    cookies.put(key, value);
    return this;
  }

  @Override
  public TrustedHttpRequestExecutor removeCookie(String key) {
    cookies.remove(key);
    return this;
  }


  private RequestConfig buildRequestConfig(int timeoutInMs) {
    return RequestConfig.custom()
        .setConnectTimeout(timeoutInMs)
        .setConnectionRequestTimeout(timeoutInMs)
        .setSocketTimeout(timeoutInMs).build();
  }

  private SSLContext trustAllSslContext()
      throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
    return new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
      public boolean isTrusted(X509Certificate[] arg0, String arg1)
          throws CertificateException {
        return true;
      }
    }).build();
  }

  private void setCookies(HttpUriRequest request) {
    Collection<String> cookieList = Collections2.transform(cookies.entrySet(),
        new Function<Entry<String, String>, String>() {
          @Override
          public String apply(Entry<String, String> input) {
            return String.format("%s=%s", input.getKey(), input.getValue());
          }
        });
    request.addHeader(COOKIE, StringUtils.join(cookieList, "; "));
  }

  private void setHeaders(HttpUriRequest request) {
    for (Entry<String, String> entry : headers.entrySet()) {
      request.addHeader(entry.getKey(), entry.getValue());
    }
  }


}
