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
import org.apache.http.client.HttpClient;
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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@PrepareForTest({CloseableHttpClient.class, HttpClientBuilder.class, HttpClients.class, CloseableHttpResponse.class,
        StatusLine.class})
@RunWith(PowerMockRunner.class)
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
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to use get method query data from server");
        String url = "host";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");
        String response = HttpsUtils.get(url, header);
        assertThat(response, equalTo(""));
    }

    @Test
    public void testHttpsUtil_get_normal() throws Exception {
        HttpClientBuilder hcb = PowerMock.createMock(HttpClientBuilder.class);
        CloseableHttpClient httpClient = PowerMock.createMock(CloseableHttpClient.class);
        PowerMock.mockStatic(HttpClients.class);
        EasyMock.expect(HttpClients.custom()).andReturn(hcb);
        EasyMock.expect(hcb.setSSLSocketFactory(EasyMock.anyObject(SSLConnectionSocketFactory.class))).andReturn(hcb);
        EasyMock.expect(hcb.setConnectionManager(EasyMock.anyObject(PoolingHttpClientConnectionManager.class))).andReturn(hcb);
        EasyMock.expect(hcb.setConnectionManagerShared(true)).andReturn(hcb);
        EasyMock.expect(hcb.build()).andReturn(httpClient);

        CloseableHttpResponse response = PowerMock.createMock(CloseableHttpResponse.class);
        EasyMock.expect(httpClient.execute(EasyMock.anyObject(HttpRequestBase.class))).andReturn(response);
        StatusLine sl = PowerMock.createMock(StatusLine.class);
        EasyMock.expect(response.getStatusLine()).andReturn(sl);
        EasyMock.expect(sl.getStatusCode()).andReturn(HttpStatus.SC_OK);
        HttpEntity responseEntity = new StringEntity("Test");
        EasyMock.expect(response.getEntity()).andReturn(responseEntity);

        httpClient.close();
        EasyMock.expectLastCall();

        PowerMock.replayAll();


        String url = "localhost";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");

        HttpEntity entity = new StringEntity("Test");
        String res = HttpsUtils.get(url, header);

        PowerMock.verifyAll();

        assertThat(res, equalTo("Test"));
    }

    @Test
    public void testHttpsUtil_post_excepiton() throws Exception {
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to use post method query data from server");
        String url = "host";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");
        Map<String, String> para = new HashMap<>();
        para.put("tset", "1111");
        String response = HttpsUtils.post(url, header, para, null);
        assertThat(response, equalTo(""));
    }

    @Test
    public void testHttpsUtil_post_normal() throws Exception {
        HttpClientBuilder hcb = PowerMock.createMock(HttpClientBuilder.class);
        CloseableHttpClient httpClient = PowerMock.createMock(CloseableHttpClient.class);
        PowerMock.mockStatic(HttpClients.class);
        EasyMock.expect(HttpClients.custom()).andReturn(hcb);
        EasyMock.expect(hcb.setSSLSocketFactory(EasyMock.anyObject(SSLConnectionSocketFactory.class))).andReturn(hcb);
        EasyMock.expect(hcb.setConnectionManager(EasyMock.anyObject(PoolingHttpClientConnectionManager.class))).andReturn(hcb);
        EasyMock.expect(hcb.setConnectionManagerShared(true)).andReturn(hcb);
        EasyMock.expect(hcb.build()).andReturn(httpClient);

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
        String res = HttpsUtils.post(url, header, para, entity);

        PowerMock.verifyAll();

        assertThat(res, equalTo("Test"));
    }

    @Test
    public void testHttpsUtil_getResponseEntity_input_null() throws Exception {
        PowerMock.resetAll();
        httpsUtils = PowerMock.createMock(HttpsUtils.class);
        PowerMock.replayAll();
        String actual = Whitebox.invokeMethod(httpsUtils, "getResponseEntity", null);
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

}