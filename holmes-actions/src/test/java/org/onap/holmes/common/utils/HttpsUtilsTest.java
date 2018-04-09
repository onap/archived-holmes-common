/**
 * Copyright 2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.holmes.common.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.holmes.common.exception.CorrelationException;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@PrepareForTest({CloseableHttpClient.class, HttpClientBuilder.class, HttpClients.class, CloseableHttpResponse.class,
        StatusLine.class})
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
public class HttpsUtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private HttpsUtils httpsUtils;

    @Before
    public void setUp() {
        httpsUtils = new HttpsUtils();
    }


    @Test
    public void testHttpsUtil_get_excepiton() throws Exception {
        PowerMock.resetAll();
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to query data from server through GET method!");
        String url = "host";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");
        CloseableHttpClient httpClient = HttpsUtils.getHttpClient(HttpsUtils.DEFUALT_TIMEOUT);
        HttpResponse httpResponse = HttpsUtils.get(url, header, httpClient);
        String response = HttpsUtils.extractResponseEntity(httpResponse);
        assertThat(response, equalTo(""));
    }

    @Test
    public void testHttpsUtil_get_normal() throws Exception {
        PowerMock.resetAll();
        CloseableHttpClient httpClient = PowerMock.createMock(CloseableHttpClient.class);
        CloseableHttpResponse response = PowerMock.createMock(CloseableHttpResponse.class);
        EasyMock.expect(httpClient.execute(EasyMock.anyObject(HttpRequestBase.class))).andReturn(response);
        StatusLine sl = PowerMock.createMock(StatusLine.class);
        EasyMock.expect(response.getStatusLine()).andReturn(sl);
        EasyMock.expect(sl.getStatusCode()).andReturn(HttpStatus.SC_OK);
        HttpEntity responseEntity = new StringEntity("Test");
        EasyMock.expect(response.getEntity()).andReturn(responseEntity);

        PowerMock.replayAll();


        String url = "localhost";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");

        HttpEntity entity = new StringEntity("Test");
        HttpResponse httpResponse = HttpsUtils.get(url, header, httpClient);
        String res = HttpsUtils.extractResponseEntity(httpResponse);

        PowerMock.verifyAll();

        assertThat(res, equalTo("Test"));
    }

    @Test
    public void testHttpsUtil_delete_excepiton() throws Exception {
        PowerMock.resetAll();
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to query data from server through DELETE method!");
        String url = "host";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");
        CloseableHttpClient httpClient = HttpsUtils.getHttpClient(HttpsUtils.DEFUALT_TIMEOUT);
        HttpResponse httpResponse = HttpsUtils.delete(url, header, httpClient);
        String response = HttpsUtils.extractResponseEntity(httpResponse);
        assertThat(response, equalTo(""));
    }

    @Test
    public void testHttpsUtil_delete_normal() throws Exception {
        PowerMock.resetAll();
        CloseableHttpClient httpClient = PowerMock.createMock(CloseableHttpClient.class);
        CloseableHttpResponse response = PowerMock.createMock(CloseableHttpResponse.class);
        EasyMock.expect(httpClient.execute(EasyMock.anyObject(HttpRequestBase.class))).andReturn(response);
        StatusLine sl = PowerMock.createMock(StatusLine.class);
        EasyMock.expect(response.getStatusLine()).andReturn(sl);
        EasyMock.expect(sl.getStatusCode()).andReturn(HttpStatus.SC_OK);
        HttpEntity responseEntity = new StringEntity("Test");
        EasyMock.expect(response.getEntity()).andReturn(responseEntity);

        PowerMock.replayAll();


        String url = "localhost";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");

        HttpEntity entity = new StringEntity("Test");
        HttpResponse httpResponse = HttpsUtils.delete(url, header, httpClient);
        String res = HttpsUtils.extractResponseEntity(httpResponse);

        PowerMock.verifyAll();

        assertThat(res, equalTo("Test"));
    }

    @Test
    public void testHttpsUtil_post_excepiton() throws Exception {
        PowerMock.resetAll();
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to query data from server through POST method!");
        String url = "host";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");
        Map<String, String> para = new HashMap<>();
        para.put("tset", "1111");
        CloseableHttpClient httpClient = HttpsUtils.getHttpClient(HttpsUtils.DEFUALT_TIMEOUT);
        HttpResponse httpResponse = HttpsUtils.post(url, header, para, null, httpClient);
        String response = HttpsUtils.extractResponseEntity(httpResponse);
        assertThat(response, equalTo(""));
    }

    @Test
    public void testHttpsUtil_post_normal() throws Exception {
        PowerMock.resetAll();
        CloseableHttpClient httpClient = PowerMock.createMock(CloseableHttpClient.class);
        CloseableHttpResponse response = PowerMock.createMock(CloseableHttpResponse.class);
        EasyMock.expect(httpClient.execute(EasyMock.anyObject(HttpRequestBase.class))).andReturn(response);
        StatusLine sl = PowerMock.createMock(StatusLine.class);
        EasyMock.expect(response.getStatusLine()).andReturn(sl);
        EasyMock.expect(sl.getStatusCode()).andReturn(HttpStatus.SC_OK);
        HttpEntity responseEntity = new StringEntity("Test");
        EasyMock.expect(response.getEntity()).andReturn(responseEntity);

        PowerMock.replayAll();


        String url = "localhost";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");
        Map<String, String> para = new HashMap<>();
        para.put("tset", "1111");

        HttpEntity entity = new StringEntity("Test");
        HttpResponse httpResponse = HttpsUtils.post(url, header, para, entity, httpClient);
        String res = HttpsUtils.extractResponseEntity(httpResponse);

        PowerMock.verifyAll();

        assertThat(res, equalTo("Test"));
    }

    @Test
    public void testHttpsUtil_put_excepiton() throws Exception {
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to query data from server through PUT method!");
        String url = "host";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");
        Map<String, String> para = new HashMap<>();
        para.put("tset", "1111");
        CloseableHttpClient httpClient = HttpsUtils.getHttpClient(HttpsUtils.DEFUALT_TIMEOUT);
        HttpResponse httpResponse = HttpsUtils.put(url, header, para, null, httpClient);
        String response = HttpsUtils.extractResponseEntity(httpResponse);
        assertThat(response, equalTo(""));
    }

    @Test
    public void testHttpsUtil_put_normal() throws Exception {
        PowerMock.resetAll();
        CloseableHttpClient httpClient = PowerMock.createMock(CloseableHttpClient.class);
        CloseableHttpResponse response = PowerMock.createMock(CloseableHttpResponse.class);
        EasyMock.expect(httpClient.execute(EasyMock.anyObject(HttpRequestBase.class))).andReturn(response);
        StatusLine sl = PowerMock.createMock(StatusLine.class);
        EasyMock.expect(response.getStatusLine()).andReturn(sl);
        EasyMock.expect(sl.getStatusCode()).andReturn(HttpStatus.SC_OK);
        HttpEntity responseEntity = new StringEntity("Test");
        EasyMock.expect(response.getEntity()).andReturn(responseEntity);

        PowerMock.replayAll();


        String url = "localhost";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");
        Map<String, String> para = new HashMap<>();
        para.put("tset", "1111");

        HttpEntity entity = new StringEntity("Test");
        HttpResponse httpResponse = HttpsUtils.put(url, header, para, entity, httpClient);
        String res = HttpsUtils.extractResponseEntity(httpResponse);

        PowerMock.verifyAll();

        assertThat(res, equalTo("Test"));
    }

    @Test
    public void testHttpsUtil_getResponseEntity_input_null() throws Exception {
        PowerMock.resetAll();
        httpsUtils = PowerMock.createMock(HttpsUtils.class);
        PowerMock.replayAll();
        String actual = Whitebox.invokeMethod(httpsUtils, "extractResponseEntity", null);
        PowerMock.verifyAll();
        assertThat(actual, equalTo(""));
    }


    @Test
    public void testHttpsUtil_getHttpClient_exception() throws Exception {
        PowerMock.resetAll();
        thrown.expect(Exception.class);
        Whitebox.invokeMethod(HttpsUtils.class, "getHttpClient");
        PowerMock.verifyAll();
    }

    @Test
    public void testHttpsUtil_getHttpClient_ok() throws Exception {
        PowerMock.resetAll();
        HttpsUtils.getHttpClient(HttpsUtils.DEFUALT_TIMEOUT);
        PowerMock.verifyAll();
    }

}