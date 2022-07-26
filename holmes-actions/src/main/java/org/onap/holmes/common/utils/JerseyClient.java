/**
 * Copyright 2020 - 2022 ZTE Corporation.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.holmes.common.utils;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class JerseyClient {
    static final public String PROTOCOL_HTTP = "http";
    static final public String PROTOCOL_HTTPS = "https";
    static private Logger logger = LoggerFactory.getLogger(JerseyClient.class);
    static private long DEFAULT_TIMEOUT = TimeUnit.SECONDS.toMillis(30);
    static private SSLContext SSLCONTEXT;

    static {
        try {
            SSLCONTEXT = SSLContext.getInstance("TLS");
            SSLCONTEXT.init(null, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                }

                public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("Failed to initialize the SSLContext instance!", e);
        }
    }

    private Client client;
    private Map<String, Object> headers = new HashMap();
    private Map<String, Object> parameters = new HashMap();
    private List<String> paths = new ArrayList();

    public static JerseyClient newInstance() {
        return new JerseyClient();
    }

    public static JerseyClient newInstance(long timeout) {
        return new JerseyClient(timeout);
    }

    private JerseyClient() {
        this(DEFAULT_TIMEOUT);
    }

    private JerseyClient(long timeout) {
        this.client = ClientBuilder.newBuilder()
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .sslContext(SSLCONTEXT)
                .hostnameVerifier((s1, s2) -> true)
                .build();
    }

    public JerseyClient header(String name, Object value) {
        headers.put(name, value);
        return this;
    }

    public JerseyClient headers(Map<String, Object> hds) {
        headers.putAll(hds);
        return this;
    }

    public JerseyClient queryParam(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    public JerseyClient queryParams(Map<String, Object> params) {
        parameters.putAll(params);
        return this;
    }

    public JerseyClient path(String path) {
        paths.add(path);
        return this;
    }

    public String get(String url) {
        return get(url, String.class);
    }

    public <T> T get(String url, Class<T> clazz) {

        WebTarget target = appendPaths(client.target(url));

        target = setParameters(target);

        Invocation.Builder builder = setHeaders(target.request());

        Response response = builder.get();

        if (isSuccessful(response, url)) {
            return response2Target(response, clazz);
        }

        return null;
    }

    public String post(String url) {
        return post(url, null);
    }

    public String post(String url, Entity entity) {
        return post(url, entity, String.class);
    }

    public <T> T post(String url, Entity entity, Class<T> clazz) {

        WebTarget target = appendPaths(client.target(url));

        setParameters(target);

        Invocation.Builder builder = setHeaders(target.request());

        Response response = builder.post(entity);

        if (isSuccessful(response, url)) {
            return response2Target(response, clazz);
        }

        return null;
    }

    public String put(String url, Entity entity) {
        return put(url, entity, String.class);
    }

    public <T> T put(String url, Entity entity, Class<T> clazz) {
        WebTarget target = appendPaths(client.target(url));

        setParameters(target);

        Invocation.Builder builder = setHeaders(target.request());

        Response response = builder.put(entity);

        if (isSuccessful(response, url)) {
            return response2Target(response, clazz);
        }

        return null;
    }

    public String delete(String url) {
        return delete(url, String.class);
    }

    public <T> T delete(String url, Class<T> clazz) {
        WebTarget target = appendPaths(client.target(url));

        setParameters(target);

        Invocation.Builder builder = setHeaders(target.request());

        Response response = builder.delete();

        if (isSuccessful(response, url)) {
            return response2Target(response, clazz);
        }

        return null;
    }

    private boolean isSuccessful(Response response, String url) {
        Response.StatusType statusInfo = response.getStatusInfo();
        if (statusInfo.getFamily() != Response.Status.Family.SUCCESSFUL) {
            logger.error(
                    String.format("Failed to get response from the server <%d>. " +
                                    "\nURL: %s\nCause: %s\nResponse body: %s",
                            statusInfo.getStatusCode(),
                            url,
                            statusInfo.getReasonPhrase(),
                            response.readEntity(String.class)));
            return false;
        }
        return true;
    }

    private WebTarget appendPaths(WebTarget target) {
        for (String path : paths) {
            target = target.path(path);
        }
        return target;
    }

    private Invocation.Builder setHeaders(Invocation.Builder builder) {
        Set<Map.Entry<String, Object>> entries = headers.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            builder = builder.header(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    private WebTarget setParameters(WebTarget target) {
        Set<Map.Entry<String, Object>> entries = parameters.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            target = target.queryParam(entry.getKey(), entry.getValue());
        }
        return target;
    }

    private <T> T response2Target(Response response, Class<T> clazz) {
        String responseText = response.readEntity(String.class);
        if (clazz == null || clazz == String.class) {
            return (T) responseText;
        } else {
            return GsonUtil.jsonToBean(responseText, clazz);
        }
    }
}
