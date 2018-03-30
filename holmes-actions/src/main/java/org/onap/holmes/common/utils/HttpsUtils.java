/**
 * Copyright 2017 ZTE Corporation.
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

package org.onap.holmes.common.utils;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.exception.CorrelationException;

@Slf4j
@Service
public class HttpsUtils {
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final int DEFUALT_TIMEOUT = 30000;
    private static SSLConnectionSocketFactory sslConnectionSocketFactory = null;
    private static PoolingHttpClientConnectionManager connectionManager = null;
    private static SSLContextBuilder sslContextBuilder = null;

    static{
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

    public static HttpResponse post(String url, Map<String, String> header, Map<String, String> param,
            HttpEntity entity) throws CorrelationException {
        return post(url, header, param, entity, DEFUALT_TIMEOUT);
    }

    public static HttpResponse post(String url, Map<String, String> header, Map<String, String> param,
            HttpEntity entity, int timeout) throws CorrelationException {
        HttpResponse response;
        HttpPost httpPost = new HttpPost(url);
        try {
            CloseableHttpClient httpClient = getHttpClient(timeout);
            addHeaders(header, httpPost);
            addParams(param, httpPost);
            if (entity != null) {
                httpPost.setEntity(entity);
            }
            response = executeRequest(httpClient, httpPost);
        } catch (Exception e) {
            throw new CorrelationException("Failed to query data from server through POST method!");
        }
        return response;
    }

    public static HttpResponse put(String url, Map<String, String> header, Map<String, String> param,
            HttpEntity entity) throws CorrelationException {
        return put(url, header, param, entity, DEFUALT_TIMEOUT);
    }

    public static HttpResponse put(String url, Map<String, String> header, Map<String, String> param,
            HttpEntity entity, int timeout) throws CorrelationException {
        HttpResponse response;
        HttpPut httpPut = new HttpPut(url);
        try {
            CloseableHttpClient httpClient = getHttpClient(timeout);
            addHeaders(header, httpPut);
            addParams(param, httpPut);
            if (entity != null) {
                httpPut.setEntity(entity);
            }
            response = executeRequest(httpClient, httpPut);
        } catch (Exception e) {
            throw new CorrelationException("Failed to query data from server through PUT method!");
        }
        return response;
    }

    public static HttpResponse get(String url, Map<String, String> header) throws CorrelationException {
        return get(url, header, DEFUALT_TIMEOUT);
    }

    public static HttpResponse get(String url, Map<String, String> header, int timeout) throws CorrelationException {
        HttpResponse response;
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpClient httpClient = getHttpClient(timeout);
            addHeaders(header, httpGet);
            response = executeRequest(httpClient, httpGet);
        } catch (Exception e) {
            throw new CorrelationException("Failed to query data from server through GET method!");
        }
        return response;
    }

    public static HttpResponse delete(String url, Map<String, String> header) throws CorrelationException {
        return delete(url, header, DEFUALT_TIMEOUT);
    }

    public static HttpResponse delete(String url, Map<String, String> header, int timeout) throws CorrelationException {
        HttpResponse response;
        HttpDelete httpDelete = new HttpDelete(url);
        try {
            CloseableHttpClient httpClient = getHttpClient(timeout);
            addHeaders(header, httpDelete);
            response = executeRequest(httpClient, httpDelete);
        } catch (Exception e) {
            throw new CorrelationException("Failed to query data from server through DELETE method!");
        }
        return response;
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
            e.printStackTrace();
            throw new CorrelationException("Failed to get data from server" ,e);
        } finally {
            if (httpRequest != null) {
                httpRequest.releaseConnection();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        }
        return httpResponse;
    }

    private static CloseableHttpClient getHttpClient(int timeout) throws Exception {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setConnectionManager(connectionManager)
                .setConnectionManagerShared(true)
                .build();
        return httpClient;
    }
}
