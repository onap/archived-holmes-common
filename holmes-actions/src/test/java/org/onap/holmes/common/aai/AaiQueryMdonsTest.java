/**
 * Copyright 2020 - 2021 Fujitsu, ZTE Limited.
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
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.utils.JerseyClient;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.anyObject;
import static org.junit.Assert.assertEquals;
import static org.onap.holmes.common.config.MicroServiceConfig.MSB_ADDR;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
@PrepareForTest({AaiQueryMdons.class, MicroServiceConfig.class, JerseyClient.class})
public class AaiQueryMdonsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static AaiQueryMdons aaiMdonsQuery = AaiQueryMdons.newInstance();
    private static MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

    private static JsonObject data;

    private static final String AAI_ADDR = "https://aai.onap:8443/aai/v19/";

    @BeforeClass
    public static void beforeClass() {
        System.setProperty(MSB_ADDR, "127.0.0.1:80");
        System.setProperty("ENABLE_ENCRYPT", "true");

        File file = new File(AaiQueryMdonsTest.class.getClassLoader().getResource("./aai-mdons.json").getFile());
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

        headers.add("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.add("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.add("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

    }

    @Before
    public void before() {
        Whitebox.setInternalState(aaiMdonsQuery, "headers", headers);

    }

    @After
    public void after() {
        PowerMock.resetAll();
    }

    @Test
    public void testProcessPnf() throws Exception {
        String pnfUrl = AAI_ADDR + "network/pnfs/pnf/test1?depth=all";
        String domainService = AAI_ADDR
                + "business/customers/customer/Orange/service-subscriptions/service-subscription/MDONS_OTN/service-instances/service-instance/789";
        PowerMock.resetAll();

        aaiMdonsQuery = PowerMock.createPartialMock(AaiQueryMdons.class, "getResponse");
        PowerMock.expectPrivate(aaiMdonsQuery, "getResponse", pnfUrl).andReturn(data.get("pnf-depth-all").toString());
        PowerMock.expectPrivate(aaiMdonsQuery, "getResponse", domainService)
                .andReturn(data.get("get-domain-service").toString());

        PowerMock.replayAll();
        Map<String, String> accessMap = aaiMdonsQuery.processPnf("test1");
        PowerMock.verifyAll();
        Map<String, String> verifyMap = new HashMap<>();
        verifyMap.put("123", "access-service");
        assertEquals(accessMap, verifyMap);
    }

    @Test
    public void testGetPnfName() throws Exception {
        String pnfUrl = AAI_ADDR + "network/pnfs?pnf-id=test1-id";

        Whitebox.setInternalState(aaiMdonsQuery, "headers", headers);

        aaiMdonsQuery = PowerMock.createPartialMock(AaiQueryMdons.class, "getResponse");
        PowerMock.expectPrivate(aaiMdonsQuery, "getResponse", pnfUrl).andReturn(data.get("get-pnf-by-id").toString());
        PowerMock.replayAll();
        String pnfName = Whitebox.invokeMethod(aaiMdonsQuery, "getPnfNameFromPnfId", "test1-id");

        PowerMock.verifyAll();
        assertEquals(pnfName, "test1");

    }

    @Test
    public void testUpdatelinksAccess() throws Exception {
        Map<String, String> accessMap = new HashMap<>();
        accessMap.put("123", "access-service");
        String accessService = AAI_ADDR
                + "business/customers/customer/Orange/service-subscriptions/service-subscription/MDONS_OTN/service-instances/service-instance/123";
        String linkUrl = AAI_ADDR + "network/logical-links/logical-link/link1";

        String response =
                "{\"link-name\":\"link1\",\"in-maint\":false,\"link-type\":\"inter-domain\",\"available-capacity\":\"ODU2\",\"resource-version\":\"1584338211407\",\"operational-status\":\"down\"}";

        aaiMdonsQuery = PowerMock.createPartialMock(AaiQueryMdons.class, "getResponse");

        JerseyClient mockedClient = PowerMock.createMock(JerseyClient.class);
        PowerMock.expectNew(JerseyClient.class).andReturn(mockedClient);
        EasyMock.expect(mockedClient.headers(anyObject(Map.class))).andReturn(mockedClient);
        EasyMock.expect(mockedClient.put(anyObject(String.class), anyObject(Entity.class))).andReturn("");

        PowerMock.expectPrivate(aaiMdonsQuery, "getResponse", accessService)
                .andReturn(data.get("get-access-service").toString());
        PowerMock.expectPrivate(aaiMdonsQuery, "getResponse", linkUrl).andReturn(data.get("get-inter-link").toString());

        PowerMock.replayAll();
        Whitebox.invokeMethod(aaiMdonsQuery, "updateLinksForAccessService", accessMap);

        PowerMock.verifyAll();

    }

}
