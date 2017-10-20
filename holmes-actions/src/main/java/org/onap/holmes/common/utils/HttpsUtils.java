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
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
                    new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null,
                    NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(HTTP, new PlainConnectionSocketFactory())
                    .register(HTTPS, sslConnectionSocketFactory)
                    .build();
            connectionManager = new PoolingHttpClientConnectionManager(registry);
            connectionManager.setMaxTotal(200);
        } catch (Exception e) {
            log.error("Failed to initialize the ssl builder: " + e.getMessage());
        }
    }

    public static String post(String url, Map<String, String> header, Map<String, String> param,
            HttpEntity entity) throws Exception {
        HttpResponse httpResponse = null;
        try {
            CloseableHttpClient httpClient = getHttpClient();
            HttpPost httpPost = getHttpPost(url, header, param, entity);
            httpResponse = getHttpResponse(httpClient, httpPost);
        } catch (Exception e) {
            throw new CorrelationException("Failed to use post method query data from server");
        }
        return getResponseEntity(httpResponse);
    }

    public static String get(String url, Map<String, String> header) throws Exception {
        HttpResponse httpResponse = null;
        CloseableHttpClient httpClient = null;
        HttpGet httpGet = null;
        String response = "";
        try {
            httpClient = getHttpClient();
            httpGet = getHttpGet(url, header);
            httpResponse = getHttpResponse(httpClient, httpGet);
            response = getResponseEntity(httpResponse);
        } catch (Exception e) {
            throw new CorrelationException("Failed to use get method query data from server");
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
            if (httpResponse != null) {
                httpClient.close();
            }
        }
        return response;
    }

    private static HttpPost getHttpPost(String url, Map<String, String> header,
            Map<String, String> param, HttpEntity entity) {
        HttpPost httpPost = new HttpPost(url);
        if (!header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (!param.isEmpty()) {
            List<NameValuePair> formparams = new ArrayList<>();
            for (Map.Entry<String, String> entry : param.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formparams,
                    Consts.UTF_8);
            httpPost.setEntity(urlEncodedFormEntity);
        }
        if (entity != null) {
            httpPost.setEntity(entity);
        }
        return httpPost;
    }

    private static HttpGet getHttpGet(String url, Map<String, String> header) {
        HttpGet httpGet = new HttpGet(url);
        if (!header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return httpGet;
    }

    private static String getResponseEntity(HttpResponse httpResponse) throws IOException {
        String result = "";
        if (httpResponse != null) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                result = EntityUtils.toString(resEntity);
            }
        }
        return result;
    }

    private static HttpResponse getHttpResponse(CloseableHttpClient httpClient, HttpRequestBase httpRequest)
            throws Exception {
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpRequest);
        } catch (Exception e) {
            throw new CorrelationException("Failed to get data from server");
        }
        return httpResponse;
    }

    private static CloseableHttpClient getHttpClient() throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setConnectionManager(connectionManager)
                .setConnectionManagerShared(true)
                .build();
        return httpClient;
    }
}
