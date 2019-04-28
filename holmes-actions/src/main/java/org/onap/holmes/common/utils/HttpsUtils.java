/**
 * Copyright 2017 ZTE Corporation.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.onap.holmes.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HttpsUtils {
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static SSLConnectionSocketFactory sslConnectionSocketFactory = null;
    private static PoolingHttpClientConnectionManager connectionManager = null;
    private static SSLContextBuilder sslContextBuilder = null;
    public static final int DEFUALT_TIMEOUT = 30000;

    static {
        try {
            sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] x509Certificates, String s)
                        throws CertificateException {
                    return true;
                }
            });
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(),
                    new String[]{"SSLv3", "TLSv1", "TLSv1.2"}, null,
                    NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(HTTP, new PlainConnectionSocketFactory())
                    .register(HTTPS, sslConnectionSocketFactory)
                    .build();
            connectionManager = new PoolingHttpClientConnectionManager(registry);
            connectionManager.setMaxTotal(200);
        } catch (Exception e) {
            log.error("Failed to initialize the ssl builder: " + e.getMessage(), e);
        }
    }

    public static HttpResponse get(HttpGet httpGet, Map<String, String> header, CloseableHttpClient httpClient) throws CorrelationException {
        return getGetAndDeleteResponse(httpGet, header, httpClient);
    }

    public static HttpResponse post(HttpPost httpPost, Map<String, String> header, Map<String, String> param,
                                    HttpEntity entity, CloseableHttpClient httpClient) throws CorrelationException {
        return getPostAndPutResponse(httpPost, header, param, entity, httpClient);
    }

    public static HttpResponse put(HttpPut httpPut, Map<String, String> header, Map<String, String> param,
                                   HttpEntity entity, CloseableHttpClient httpClient) throws CorrelationException {
        return getPostAndPutResponse(httpPut, header, param, entity, httpClient);
    }

    public static HttpResponse delete(HttpDelete httpDelete, Map<String, String> header, CloseableHttpClient httpClient) throws CorrelationException {
        return getGetAndDeleteResponse(httpDelete, header, httpClient);
    }

    private static void addParams(Map<String, String> param, HttpEntityEnclosingRequestBase requestBase) {
        if (!param.isEmpty()) {
            List<NameValuePair> formparams = new ArrayList<>();
            for (Map.Entry<String, String> entry : param.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formparams,
                    Consts.UTF_8);
            requestBase.setEntity(urlEncodedFormEntity);
        }
    }

    private static HttpRequestBase addHeaders(Map<String, String> header, HttpRequestBase httpRequestBase) {
        if (!header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpRequestBase.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return httpRequestBase;
    }

    private static HttpResponse getPostAndPutResponse(HttpEntityEnclosingRequestBase requestBase,
                                                      Map<String, String> header, Map<String, String> param, HttpEntity entity,
                                                      CloseableHttpClient httpClient) throws CorrelationException {
        try {
            addHeaders(header, requestBase);
            addParams(param, requestBase);
            if (entity != null) {
                requestBase.setEntity(entity);
            }
            return executeRequest(httpClient, requestBase);
        } catch (Exception e) {
            throw new CorrelationException("Failed to connect to server", e);
        }
    }

    private static HttpResponse getGetAndDeleteResponse(HttpRequestBase requestBase,
                                                        Map<String, String> header, CloseableHttpClient httpClient) throws CorrelationException {
        try {
            addHeaders(header, requestBase);
            return executeRequest(httpClient, requestBase);
        } catch (Exception e) {
            throw new CorrelationException("Failed to connect to server", e);
        }
    }

    public static String extractResponseEntity(HttpResponse httpResponse)
            throws CorrelationException, IOException {
        String result = "";
        if (httpResponse != null) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                result = EntityUtils.toString(resEntity);
            } else {
                throw new CorrelationException("Get an error status from server : " + statusCode);
            }
        }
        return result;
    }

    private static HttpResponse executeRequest(CloseableHttpClient httpClient, HttpRequestBase httpRequest)
            throws CorrelationException, IOException {
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpRequest);
        } catch (Exception e) {
            throw new CorrelationException("Failed to get data from server", e);
        }
        return httpResponse;
    }

    public static CloseableHttpClient getConditionalHttpsClient(int timeout) {
        HttpClientBuilder builder = getHttpClientBuilder(timeout);
        if (isHttpsEnabled()) {
            builder.setSSLSocketFactory(sslConnectionSocketFactory);
        }

        return builder.build();
    }

    public static CloseableHttpClient getHttpsClient(int timeout) {
        HttpClientBuilder builder = getHttpClientBuilder(timeout);
        return builder.setSSLSocketFactory(sslConnectionSocketFactory).build();
    }

    private static HttpClientBuilder getHttpClientBuilder(int timeout) {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .setConnectionManager(connectionManager)
                .setConnectionManagerShared(true);
    }

    public static boolean isHttpsEnabled() {
        return Boolean.valueOf(MicroServiceConfig.getEnv("ENABLE_ENCRYPT"));
    }
}
