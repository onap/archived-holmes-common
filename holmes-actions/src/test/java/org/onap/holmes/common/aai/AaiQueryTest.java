/**
 * Copyright 2017-2020 ZTE Corporation.
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

package org.onap.holmes.common.aai;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.exception.HttpException;
import org.onap.holmes.common.utils.JerseyClient;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Map;

import static org.easymock.EasyMock.anyObject;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;


@PrepareForTest({AaiQuery.class, MicroServiceConfig.class})
@PowerMockIgnore("javax.net.ssl.*")
@RunWith(PowerMockRunner.class)
public class AaiQueryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AaiQuery aaiQuery;
    private AaiResponseUtil aaiResponseUtil;

    @BeforeClass
    static public void before() {
        System.setProperty("ENABLE_ENCRYPT", "true");
    }

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

        assertThat(vnfEntity, is(nullValue()));
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

        String url = "https://aai.onap:8443/aai/v11/cloud-infrastructure";

        JerseyClient mockedClient = PowerMock.createMock(JerseyClient.class);
        PowerMock.expectNew(JerseyClient.class).andReturn(mockedClient);
        EasyMock.expect(mockedClient.headers(EasyMock.anyObject(Map.class))).andReturn(mockedClient);
        EasyMock.expect(mockedClient.get(url)).andReturn("{}");

        PowerMock.expectPrivate(aaiQuery, "getVmResourceLinks", "test1", "test2")
                .andReturn("/aai/v11/cloud-infrastructure");

        PowerMock.replayAll();

        VmEntity vmEntity = Whitebox.invokeMethod(aaiQuery, "getAaiVmData", "test1", "test2");

        PowerMock.verifyAll();

        assertThat(vmEntity, is(nullValue()));
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
    public void testAaiQuery_getResourceLinksResponse() throws Exception {
        PowerMock.resetAll();
        aaiQuery = PowerMock.createPartialMock(AaiQuery.class, "getResponse");

        aaiResponseUtil = new AaiResponseUtil();
        Whitebox.setInternalState(aaiQuery, "aaiResponseUtil", aaiResponseUtil);

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

        String url = "host_url";

        JerseyClient mockedClient = PowerMock.createMock(JerseyClient.class);
        PowerMock.expectNew(JerseyClient.class).andReturn(mockedClient);
        EasyMock.expect(mockedClient.headers(EasyMock.anyObject(Map.class))).andReturn(mockedClient);
        EasyMock.expect(mockedClient.get(url)).andReturn("");

        PowerMock.replayAll();
        String resource = Whitebox.invokeMethod(aaiQuery, "getResponse", "host_url");
        PowerMock.verifyAll();

        assertThat(resource, equalTo(""));
    }

    @Test
    public void testAaiQuery_getResponse_exception() throws Exception {
        PowerMock.resetAll();
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to get data from aai");
        aaiQuery = new AaiQuery();

        String url = "host_url";

        JerseyClient mockedClient = PowerMock.createMock(JerseyClient.class);
        PowerMock.expectNew(JerseyClient.class).andReturn(mockedClient);
        EasyMock.expect(mockedClient.headers(EasyMock.anyObject(Map.class))).andReturn(mockedClient);
        EasyMock.expect(mockedClient.get(url)).andThrow(new HttpException(404, "Not Found"));

        PowerMock.replayAll();
        String resource = Whitebox.invokeMethod(aaiQuery, "getResponse", url);
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
    public void testAaiQuery_getBaseUrl_aaiurl() throws Exception {
        PowerMock.resetAll();
        aaiQuery = new AaiQuery();

        PowerMock.mockStatic(MicroServiceConfig.class);

        PowerMock.replayAll();
        String actual = Whitebox.invokeMethod(aaiQuery, "getBaseUrl", "/url");
        PowerMock.verifyAll();

        assertThat(actual, equalTo("https://aai.onap:8443/url"));
    }
}
