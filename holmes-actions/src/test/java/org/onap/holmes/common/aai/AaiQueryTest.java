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

package org.onap.holmes.common.aai;
import static org.easymock.EasyMock.anyObject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.HttpsUtils;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


@PrepareForTest({AaiQuery.class, HttpsUtils.class, MicroServiceConfig.class})
@RunWith(PowerMockRunner.class)
public class AaiQueryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AaiQuery aaiQuery;
    private AaiResponseUtil aaiResponseUtil;

    @Test
    public void testAaiQuery_getAaiVnfData_ok() throws Exception {
        PowerMock.resetAll();
        aaiQuery = PowerMock.createPartialMock(AaiQuery.class, "getVnfDataResponse");
        aaiResponseUtil = new AaiResponseUtil();
        Whitebox.setInternalState(aaiQuery, "aaiResponseUtil", aaiResponseUtil);

        PowerMock.expectPrivate(aaiQuery, "getVnfDataResponse", "test1", "test2").andReturn("{}");

        PowerMock.replayAll();
        VnfEntity vnfEntity = Whitebox.invokeMethod(aaiQuery, "getAaiVnfData", "test1", "test2");
        PowerMock.verifyAll();

        assertThat(vnfEntity == null, equalTo(true));
    }

    @Test
    public void testAaiQuery_getAaiVnfData_exception() throws Exception {
        PowerMock.resetAll();
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to convert aai vnf response data to vnf entity");
        aaiQuery = PowerMock.createPartialMock(AaiQuery.class, "getVnfDataResponse");
        aaiResponseUtil = new AaiResponseUtil();
        Whitebox.setInternalState(aaiQuery, "aaiResponseUtil", aaiResponseUtil);
        PowerMock.expectPrivate(aaiQuery, "getVnfDataResponse", "test1", "test2")
                .andReturn("{***}");

        PowerMock.replayAll();
        aaiQuery.getAaiVnfData("test1", "test2");
        PowerMock.verifyAll();
    }

    @Test
    public void testAaiQuery_getAaiVmData_ok() throws Exception {
        PowerMock.resetAll();
        aaiQuery = PowerMock.createPartialMock(AaiQuery.class, "getVmResourceLinks");
        aaiResponseUtil = new AaiResponseUtil();
        Whitebox.setInternalState(aaiQuery, "aaiResponseUtil", aaiResponseUtil);
        PowerMockito.mockStatic(HttpsUtils.class);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.put("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.put("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.put("Accept", "application/json");
        String url = "host_url";
        when(HttpsUtils.get(url, headers)).thenReturn("{}");

        PowerMockito.mockStatic(MicroServiceConfig.class);
        when(MicroServiceConfig.getMsbServerAddr()).thenReturn("host_url");

        PowerMock.expectPrivate(aaiQuery, "getVmResourceLinks", "test1", "test2").andReturn("");
        PowerMock.replayAll();
        VmEntity vmEntity = Whitebox.invokeMethod(aaiQuery, "getAaiVmData", "test1", "test2");
        PowerMock.verifyAll();

        assertThat(vmEntity == null, equalTo(true));
    }



    @Test
    public void testAaiQuery_getAaiVmData_exception() throws Exception {
        PowerMock.resetAll();
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to convert aai vm response data to vm entity");
        aaiQuery = PowerMock.createPartialMock(AaiQuery.class, "getVmResourceLinks");

        aaiResponseUtil = new AaiResponseUtil();
        Whitebox.setInternalState(aaiQuery, "aaiResponseUtil", aaiResponseUtil);

        PowerMockito.mockStatic(HttpsUtils.class);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.put("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.put("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.put("Accept", "application/json");
        String url = "host_url";

        when(HttpsUtils.get(url, headers)).thenReturn("");

        PowerMockito.mockStatic(MicroServiceConfig.class);
        when(MicroServiceConfig.getMsbServerAddr()).thenReturn("host_url");

        PowerMock.expectPrivate(aaiQuery, "getVmResourceLinks", "test1", "test2").andReturn("");

        PowerMock.replayAll();
        Whitebox.invokeMethod(aaiQuery, "getAaiVmData", "test1", "test2");
        PowerMock.verifyAll();
    }

    @Test
    public void testAaiQuery_getAaiVmData_httpsutils_exception() throws Exception {
        PowerMock.resetAll();
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to get data from aai");
        aaiQuery = PowerMock.createPartialMock(AaiQuery.class, "getVmResourceLinks");

        aaiResponseUtil = new AaiResponseUtil();
        Whitebox.setInternalState(aaiQuery, "aaiResponseUtil", aaiResponseUtil);

        PowerMockito.mockStatic(HttpsUtils.class);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.put("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.put("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.put("Accept", "application/json");
        String url = "host_url";

        when(HttpsUtils.get(url, headers)).thenThrow(new CorrelationException(""));

        PowerMockito.mockStatic(MicroServiceConfig.class);
        when(MicroServiceConfig.getMsbServerAddr()).thenReturn("host_url");

        PowerMock.expectPrivate(aaiQuery, "getVmResourceLinks", "test1", "test2").andReturn("");
        PowerMock.replayAll();
        Whitebox.invokeMethod(aaiQuery, "getAaiVmData", "test1", "test2");
        PowerMock.verifyAll();
    }

    @Test
    public void testAaiQuery_getVmResourceLinks_ok() throws Exception {
        PowerMock.resetAll();
        aaiQuery = PowerMock.createPartialMock(AaiQuery.class, "getResourceLinksResponse");

        aaiResponseUtil = new AaiResponseUtil();
        Whitebox.setInternalState(aaiQuery, "aaiResponseUtil", aaiResponseUtil);

        String result = "{\"result-data\":[{\"resource-type\":\"vserver\",\"resource-link\":\"le-vserver-id-val-51834\"}]}";

        PowerMock.expectPrivate(aaiQuery, "getResourceLinksResponse", "test1", "test2").andReturn(result);
        PowerMock.replayAll();
        String resource = Whitebox.invokeMethod(aaiQuery, "getVmResourceLinks", "test1", "test2");
        PowerMock.verifyAll();

        assertThat(resource, equalTo("le-vserver-id-val-51834"));
    }

    @Test
    public void testAaiQuery_getVmResourceLinks_exception() throws Exception {
        PowerMock.resetAll();
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to get aai resource link");
        aaiQuery = PowerMock.createPartialMock(AaiQuery.class, "getResourceLinksResponse");

        aaiResponseUtil = new AaiResponseUtil();
        Whitebox.setInternalState(aaiQuery, "aaiResponseUtil", aaiResponseUtil);

        PowerMock.expectPrivate(aaiQuery, "getResourceLinksResponse", "test1", "test2").andReturn("");
        PowerMock.replayAll();
        String resource = Whitebox.invokeMethod(aaiQuery, "getVmResourceLinks", "test1", "test2");
        PowerMock.verifyAll();

        assertThat(resource, equalTo("le-vserver-id-val-51834"));
    }

    @Test
    public void testAaiQuery_getResourceLinksResponse() throws Exception {
        PowerMock.resetAll();
        aaiQuery = PowerMock.createPartialMock(AaiQuery.class, "getResponse");

        aaiResponseUtil = new AaiResponseUtil();
        Whitebox.setInternalState(aaiQuery, "aaiResponseUtil", aaiResponseUtil);

        PowerMockito.mockStatic(MicroServiceConfig.class);
        when(MicroServiceConfig.getMsbServerAddr()).thenReturn("host_url");

        PowerMock.expectPrivate(aaiQuery, "getResponse", anyObject(String.class)).andReturn("").anyTimes();
        PowerMock.replayAll();
        String resource = Whitebox.invokeMethod(aaiQuery, "getResourceLinksResponse", "test1", "test2");
        PowerMock.verifyAll();

        assertThat(resource, equalTo(""));
    }

    @Test
    public void testAaiQuery_getVnfDataResponse() throws Exception {
        PowerMock.resetAll();
        aaiQuery = PowerMock.createPartialMock(AaiQuery.class, "getResponse");

        aaiResponseUtil = new AaiResponseUtil();
        Whitebox.setInternalState(aaiQuery, "aaiResponseUtil", aaiResponseUtil);

        PowerMockito.mockStatic(MicroServiceConfig.class);
        when(MicroServiceConfig.getMsbServerAddr()).thenReturn("host_url");

        PowerMock.expectPrivate(aaiQuery, "getResponse", anyObject(String.class)).andReturn("").anyTimes();
        PowerMock.replayAll();
        String resource = Whitebox.invokeMethod(aaiQuery, "getVnfDataResponse", "test1", "test2");
        PowerMock.verifyAll();

        assertThat(resource, equalTo(""));
    }

    @Test
    public void testAaiQuery_getResponse_ok() throws Exception {
        PowerMock.resetAll();
        aaiQuery = new AaiQuery();
        PowerMockito.mockStatic(HttpsUtils.class);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.put("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.put("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.put("Accept", "application/json");
        String url = "host_url";

        when(HttpsUtils.get(url, headers)).thenReturn("");

        PowerMock.replayAll();
        String resource = Whitebox.invokeMethod(aaiQuery, "getResponse", "host_url");
        PowerMock.verifyAll();

        assertThat(resource, equalTo(""));
    }

    @Test
    public void testAaiQuery_getResponse_exceptioin() throws Exception {
        PowerMock.resetAll();
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to get data from aai");
        aaiQuery = new AaiQuery();

        PowerMockito.mockStatic(HttpsUtils.class);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.put("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.put("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.put("Accept", "application/json");
        String url = "host_url";

        when(HttpsUtils.get(url, headers)).thenThrow(new CorrelationException(""));

        PowerMock.replayAll();
        String resource = Whitebox.invokeMethod(aaiQuery, "getResponse", "host_url");
        PowerMock.verifyAll();

        assertThat(resource, equalTo(""));

    }

    @Test
    public void testAaiQuery_getHeaders() throws Exception {
        PowerMock.resetAll();
        aaiQuery = new AaiQuery();
        PowerMock.replayAll();
        Map actual = Whitebox.invokeMethod(aaiQuery, "getHeaders");
        PowerMock.verifyAll();

        assertThat(actual.get("X-TransactionId"), equalTo("9999"));
        assertThat(actual.get("X-FromAppId"), equalTo("jimmy-postman"));
        assertThat(actual.get("Authorization"), equalTo("Basic QUFJOkFBSQ=="));
        assertThat(actual.get("Accept"), equalTo("application/json"));
    }

    @Test
    public void testAaiQuery_getBaseUrl_msb() throws Exception {
        PowerMock.resetAll();
        aaiQuery = new AaiQuery();

        PowerMockito.mockStatic(MicroServiceConfig.class);
        when(MicroServiceConfig.getMsbServerAddr()).thenReturn("msb");
        when(MicroServiceConfig.getServiceAddrInfoFromCBS("nihao")).thenReturn("");

        PowerMock.replayAll();
        String actual = Whitebox.invokeMethod(aaiQuery,"getBaseUrl", "url");
        PowerMock.verifyAll();
        assertThat(actual, equalTo("msburl"));
    }

    @Test
    public void testAaiQuery_getBaseUrl_aaiurl() throws Exception {
        PowerMock.resetAll();
        aaiQuery = new AaiQuery();

        PowerMockito.mockStatic(MicroServiceConfig.class);
        when(MicroServiceConfig.getMsbServerAddr()).thenThrow(new NullPointerException());
        when(MicroServiceConfig.getServiceAddrInfoFromCBS("aai_config")).thenReturn("aai");

        PowerMock.replayAll();
        String actual = Whitebox.invokeMethod(aaiQuery,"getBaseUrl", "url");
        System.out.println(actual);
        PowerMock.verifyAll();
        assertThat(actual, equalTo("https:\\\\aaiurl"));
    }

    @Test
    public void testAaiQuery_getBaseUrl_exception() throws Exception {
        PowerMock.resetAll();
        aaiQuery = new AaiQuery();

        PowerMockito.mockStatic(MicroServiceConfig.class);
        when(MicroServiceConfig.getMsbServerAddr()).thenThrow(new NullPointerException());
        when(MicroServiceConfig.getServiceAddrInfoFromCBS("aai_config"))
                .thenThrow(new NullPointerException());

        PowerMock.replayAll();
        String actual = Whitebox.invokeMethod(aaiQuery,"getBaseUrl", "url");
        System.out.println(actual);
        PowerMock.verifyAll();
        assertThat(actual, equalTo(""));
    }

    @Test
    public void testAaiQuery_getMsbSuffixAddr_Ok() throws Exception {
        PowerMock.resetAll();
        String url = "/aai/v11/network/generic-vnfs/generic-vnf?";
        String expect = "/aai/aai-network/v11/generic-vnfs/generic-vnf?";
        aaiQuery = new AaiQuery();
        PowerMock.replayAll();
        String actual = Whitebox.invokeMethod(aaiQuery, "getMsbSuffixAddr", url);
        PowerMock.verifyAll();
        assertThat(actual, equalTo(expect));
    }
}
