/**
 * Copyright 2018-2021 ZTE Corporation.
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.easymock.EasyMock;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.JerseyClient;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.onap.holmes.common.config.MicroServiceConfig.MSB_ADDR;


@RunWith(PowerMockRunner.class)
@PrepareForTest(JerseyClient.class)
public class AaiQuery4CcvpnTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static JsonObject data;

    private static AaiQuery4Ccvpn aai = AaiQuery4Ccvpn.newInstance();

    private static Map<String, Object> headers = new HashMap<>();

    private static JerseyClient client;

    @BeforeClass
    static public void beforeClass() {
        System.setProperty(MSB_ADDR, "127.0.0.1:80");

        File file = new File(AaiQuery4CcvpnTest.class.getClassLoader().getResource("./ccvpn.data.json").getFile());
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            reader.lines().forEach(l -> sb.append(l));
            data = JsonParser.parseString(sb.toString()).getAsJsonObject();
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

        headers.put("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.put("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.put("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        Whitebox.setInternalState(aai, "headers", headers);
    }

    @Before
    public void before() {
        PowerMock.mockStatic(JerseyClient.class);
        client = PowerMock.createMock(JerseyClient.class);
        EasyMock.expect(JerseyClient.newInstance()).andReturn(client).anyTimes();
    }

    @After
    public void after() {
        PowerMock.resetAll();
    }

    @Test
    public void test_getPath() throws Exception {
        String path = "/aai/v14/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances?service-instance-id={servId}";
        String ret = Whitebox.invokeMethod(aai, "getPath", path);
        assertThat(ret, equalTo("/api/aai-business/v14/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances?service-instance-id={servId}"));
    }

    @Test
    public void test_getLogicLink() {
        mockGetMethod(data.get("logic-link").toString());

        PowerMock.replayAll();

        String linkId = aai.getLogicLink("network-1", "pnf-1", "interface-1", "DOWN");

        PowerMock.verifyAll();

        assertThat(linkId, equalTo("logic-link-1"));

    }


    @Test
    public void test_getServiceInstance() {
        mockGetMethod(data.get("vpn-binding").toString());
        mockGetMethod(data.get("connectivity").toString());
        mockGetMethod(data.get("service-instance-by-connectivity").toString());
        mockGetMethod(data.get("service-instances-by-service-type").toString());

        PowerMock.replayAll();

        JsonObject instance = aai.getServiceInstance("network-1", "pnf-1", "interface-1", "DOWN");

        PowerMock.verifyAll();

        assertThat(instance.get("service-instance-id").getAsString(), equalTo("some id 1"));
        assertThat(instance.get("globalSubscriberId").getAsString(), equalTo("e151059a-d924-4629-845f-264db19e50b4"));
        assertThat(instance.get("serviceType").getAsString(), equalTo("volte"));
    }

    @Test
    public void test_updateTerminalPointStatus() throws CorrelationException {
        mockGetMethod(data.toString());
        mockPutMethod("ok");

        PowerMock.replayAll();

        aai.updateTerminalPointStatus("network-1", "pnf-1", "if-1", new HashMap<>());

        PowerMock.verifyAll();
    }


    @Test
    public void test_updateLogicLinkStatus() {
        mockGetMethod(data.toString());
        mockPutMethod("ok");

        PowerMock.replayAll();

        aai.updateLogicLinkStatus("link-1", new HashMap<>());

        PowerMock.verifyAll();
    }

    private void mockGetMethod(String ret) {
        EasyMock.expect(client.path(anyString())).andReturn(client);
        EasyMock.expect(client.headers(anyObject())).andReturn(client);
        EasyMock.expect(client.get(anyString())).andReturn(ret);
    }

    private void mockPutMethod(String ok) {
        EasyMock.expect(client.path(anyString())).andReturn(client);
        EasyMock.expect(client.headers(anyObject())).andReturn(client);
        EasyMock.expect(client.put(anyString(), anyObject())).andReturn(ok);
    }
}