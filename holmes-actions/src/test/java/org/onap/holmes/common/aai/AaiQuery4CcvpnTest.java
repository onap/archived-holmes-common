/**
 * Copyright 2018 ZTE Corporation.
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

package org.onap.holmes.common.aai;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.easymock.EasyMock;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.ws.rs.client.*;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.onap.holmes.common.config.MicroServiceConfig.MSB_ADDR;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ClientBuilder.class, Client.class, Builder.class, WebTarget.class, Response.class})
public class AaiQuery4CcvpnTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static JSONObject data;

    private static AaiQuery4Ccvpn aai = AaiQuery4Ccvpn.newInstance();

    private static MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    private static Client client;
    private static WebTarget webTarget;
    private static Builder builder;
    private static Response response;

    @BeforeClass
    static public void beforeClass() {
        System.setProperty(MSB_ADDR, "127.0.0.1:80");

        File file = new File(AaiQuery4CcvpnTest.class.getClassLoader().getResource("./ccvpn.data.json").getFile());
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            reader.lines().forEach(l -> sb.append(l));
            data = JSONObject.parseObject(sb.toString());
        } catch (FileNotFoundException e) {
            // Do nothing
        } catch (IOException e) {
            // Do nothing
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }

        headers.add("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.add("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.add("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.add("Accept", "application/json");
        Whitebox.setInternalState(aai, "headers", headers);
    }

    @Before
    public void before() {
        PowerMock.mockStatic(ClientBuilder.class);
        client = PowerMock.createMock(Client.class);
        webTarget = PowerMock.createMock(WebTarget.class);
        builder = PowerMock.createMock(Builder.class);
        response = PowerMock.createMock(Response.class);
    }

    @After
    public void after() {
        PowerMock.resetAll();
    }

    @Test
    public void test_getPath() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String path = "/aai/v14/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances?service-instance-id={servId}";

        Method method = AaiQuery4Ccvpn.class.getDeclaredMethod("getPath", String.class);
        method.setAccessible(true);

        String ret = (String) method.invoke(aai, path);

        assertThat(ret, equalTo("/api/aai-business/v14/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances?service-instance-id={servId}"));

    }

    @Test
    public void test_getLogicLink_exception() {
        mockGetMethod();
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.NOT_FOUND).times(2);

        thrown.expect(RuntimeException.class);

        PowerMock.replayAll();

        String linkId = aai.getLogicLink("network-1", "pnf-1", "interface-1", "DOWN");

        PowerMock.verifyAll();

        assertThat(linkId, equalTo("logic-link-1"));

    }

    @Test
    public void test_getLogicLink() {
        mockGetMethod();
        EasyMock.expect(response.getEntity()).andReturn(data.getJSONObject("logic-link"));
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        PowerMock.replayAll();

        String linkId = aai.getLogicLink("network-1", "pnf-1", "interface-1", "DOWN");

        PowerMock.verifyAll();

        assertThat(linkId, equalTo("logic-link-1"));

    }

    @Test
    public void test_getServiceInstances_exception() {
        mockGetMethod();
        EasyMock.expect(response.getEntity()).andReturn(data.getJSONObject("vpn-binding"));
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        mockGetMethod();
        EasyMock.expect(response.getEntity()).andReturn(data.getJSONObject("connectivity"));
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        mockGetMethod();
        EasyMock.expect(response.getEntity()).andReturn(data.getJSONObject("service-instance-by-connectivity"));
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        mockGetMethod();
        EasyMock.expect(response.getEntity()).andReturn(data.getJSONObject("service-instances-by-service-type"));
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.NOT_FOUND).times(2);

        mockGetMethod();
        EasyMock.expect(response.readEntity(String.class)).andReturn(data.getJSONObject("service-instance").toString());
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.NOT_FOUND).times(2);

        thrown.expect(RuntimeException.class);

        PowerMock.replayAll();

        JSONArray instances = aai.getServiceInstances("network-1", "pnf-1", "interface-1", "DOWN");

        PowerMock.verifyAll();

        assertThat(instances, equalTo("logic-link-1"));

    }

    @Test
    public void test_getServiceInstances() {
        mockGetMethod();
        EasyMock.expect(response.getEntity()).andReturn(data.getJSONObject("vpn-binding"));
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        mockGetMethod();
        EasyMock.expect(response.getEntity()).andReturn(data.getJSONObject("connectivity"));
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        mockGetMethod();
        EasyMock.expect(response.getEntity()).andReturn(data.getJSONObject("service-instance-by-connectivity"));
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        mockGetMethod();
        EasyMock.expect(response.getEntity()).andReturn(data.getJSONObject("service-instances-by-service-type"));
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        mockGetMethod();
        EasyMock.expect(response.readEntity(String.class)).andReturn(data.getJSONObject("service-instance").toString());
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);
        mockGetMethod();
        EasyMock.expect(response.readEntity(String.class)).andReturn(data.getJSONObject("service-instance").toString());
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);
        mockGetMethod();
        EasyMock.expect(response.readEntity(String.class)).andReturn(data.getJSONObject("service-instance").toString());
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        PowerMock.replayAll();

        JSONArray instances = aai.getServiceInstances("network-1", "pnf-1", "interface-1", "DOWN");

        PowerMock.verifyAll();

        assertThat(instances.getJSONObject(0).getString("service-instance-id"), equalTo("some id 1"));
        assertThat(instances.getJSONObject(1).getString("service-instance-id"), equalTo("some id 2"));
        assertThat(instances.getJSONObject(2).getString("service-instance-id"), equalTo("some id 3"));
        assertThat(instances.getJSONObject(0).getString("input-parameters"), equalTo("This is the service instance recreation input looked up by CL."));
        assertThat(instances.getJSONObject(0).getString("globalSubscriberId"), equalTo("e151059a-d924-4629-845f-264db19e50b4"));
        assertThat(instances.getJSONObject(0).getString("serviceType"), equalTo("volte"));
    }

    @Test
    public void test_getServiceInstances_1() throws Exception {
        mockGetMethod();
        EasyMock.expect(response.getEntity()).andReturn(data.getJSONObject("service-instances-by-service-type"));
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        PowerMock.replayAll();

        JSONArray instances = (JSONArray) Whitebox.invokeMethod(aai, "getServiceInstances",
                "custom-1", "service-type-1");

        PowerMock.verifyAll();

        assertThat(instances.getJSONObject(0).getString("service-instance-id"), equalTo("some id 1"));
        assertThat(instances.getJSONObject(1).getString("service-instance-id"), equalTo("some id 2"));
        assertThat(instances.getJSONObject(2).getString("service-instance-id"), equalTo("some id 3"));
    }

    @Test
    public void test_getServiceInstances_1_exception() throws Exception {
        mockGetMethod();
        EasyMock.expect(response.getEntity()).andReturn(data.getJSONObject("service-instances-by-service-type"));
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.NOT_FOUND).times(2);

        thrown.expect(CorrelationException.class);

        PowerMock.replayAll();

        JSONArray instances = (JSONArray) Whitebox.invokeMethod(aai, "getServiceInstances",
                "custom-1", "service-type-1");

        PowerMock.verifyAll();

        assertThat(instances.getJSONObject(0).getString("service-instance-id"), equalTo("some id 1"));
        assertThat(instances.getJSONObject(1).getString("service-instance-id"), equalTo("some id 2"));
        assertThat(instances.getJSONObject(2).getString("service-instance-id"), equalTo("some id 3"));
    }

    @Test
    public void test_updateTerminalPointStatus() throws CorrelationException {
        mockPatchMethod();
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        PowerMock.replayAll();

        aai.updateTerminalPointStatus("network-1", "pnf-1", "if-1", new HashMap<>());

        PowerMock.verifyAll();
    }

    @Test
    public void test_updateTerminalPointStatus_exception() throws CorrelationException {
        mockPatchMethod();
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.NOT_FOUND).times(2);

        thrown.expect(CorrelationException.class);

        PowerMock.replayAll();

        aai.updateTerminalPointStatus("network-1", "pnf-1", "if-1", new HashMap<>());

        PowerMock.verifyAll();
    }

    @Test
    public void test_updateLogicLinkStatus() throws CorrelationException {
        mockPatchMethod();
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        PowerMock.replayAll();

        aai.updateLogicLinkStatus("link-1", new HashMap<>());

        PowerMock.verifyAll();
    }

    @Test
    public void test_updateLogicLinkStatus_exception() throws CorrelationException {
        mockPatchMethod();
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.NOT_FOUND).times(2);

        thrown.expect(CorrelationException.class);

        PowerMock.replayAll();

        aai.updateLogicLinkStatus("link-1", new HashMap<>());

        PowerMock.verifyAll();

    }

    private void mockGetMethod() {
        initCommonMock();
        EasyMock.expect(builder.get()).andReturn(response);
    }

    private void mockPatchMethod() {
        initCommonMock();
        Invocation invocation = PowerMock.createMock(Invocation.class);
        EasyMock.expect(builder.build(EasyMock.anyObject(String.class), EasyMock.anyObject(Entity.class))).andReturn(invocation);
        EasyMock.expect(invocation.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)).andReturn(invocation);
        EasyMock.expect(invocation.invoke()).andReturn(response);
    }

    private void initCommonMock() {
        EasyMock.expect(ClientBuilder.newClient()).andReturn(client);
        EasyMock.expect(client.target(EasyMock.anyObject(String.class))).andReturn(webTarget);
        EasyMock.expect(webTarget.path(EasyMock.anyObject(String.class))).andReturn(webTarget);
        EasyMock.expect(webTarget.request()).andReturn(builder);
        EasyMock.expect(builder.headers(headers)).andReturn(builder);
    }
}