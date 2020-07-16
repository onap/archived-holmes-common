/*-
 * ============LICENSE_START=======================================================
 * org.onap.holmes.common.aai
 * ================================================================================
 * Copyright (C) 2018-2019 Huawei. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
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
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.*;

import static org.onap.holmes.common.config.MicroServiceConfig.MSB_ADDR;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ClientBuilder.class, Client.class, Invocation.Builder.class, WebTarget.class, Response.class})
public class AaiQuery4Ccvpn2Test {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static JsonObject data;

    private static AaiQuery4Ccvpn2 aai = AaiQuery4Ccvpn2.newInstance();

    private static MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    private static Client client;
    private static WebTarget webTarget;
    private static Invocation.Builder builder;
    private static Response response;

    @BeforeClass
    static public void beforeClass() {
        System.setProperty(MSB_ADDR, "127.0.0.1:80");

        File file = new File(AaiQuery4Ccvpn2Test.class.getClassLoader().getResource("./ccvpn2.data.json").getFile());
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
        Whitebox.setInternalState(aai, "headers", headers);
    }

    @Before
    public void before() {
        PowerMock.mockStatic(ClientBuilder.class);
        client = PowerMock.createMock(Client.class);
        webTarget = PowerMock.createMock(WebTarget.class);
        builder = PowerMock.createMock(Invocation.Builder.class);
        response = PowerMock.createMock(Response.class);
    }

    @After
    public void after() {
        PowerMock.resetAll();
    }

    @Test
    public void test_getServiceInstances_exception() throws CorrelationException {
        mockGetMethod();
        EasyMock.expect(response.readEntity(String.class)).andReturn(data.get("site-resources").toString());
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        mockGetMethod();
        EasyMock.expect(response.readEntity(String.class)).andReturn(data.get("499hkg9933NNN").toString());
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        mockGetMethod();
        EasyMock.expect(response.readEntity(String.class)).andReturn(data.get("499hkg9933NNN").toString());
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        PowerMock.replayAll();

        aai.getSiteServiceInstance("HkHubONSDEMOBJHKCustomer");

        PowerMock.verifyAll();
    }

    @Test
    public void test_getServiceInstancesNull_exception() throws CorrelationException {
        mockGetMethod();
        EasyMock.expect(response.readEntity(String.class)).andReturn(data.get("site-resources1").toString());
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        mockGetMethod();
        EasyMock.expect(response.readEntity(String.class)).andReturn(data.get("499hkg9933NNN").toString());
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        mockGetMethod();
        EasyMock.expect(response.readEntity(String.class)).andReturn(data.get("499hkg9933NNN").toString());
        EasyMock.expect(response.getStatusInfo()).andReturn(Response.Status.OK);

        PowerMock.replayAll();

        aai.getSiteServiceInstance("HkHubONSDEMOSZHKCustomer");
    }


    private void mockGetMethod() {
        initCommonMock();
        EasyMock.expect(builder.get()).andReturn(response);
    }

    private void initCommonMock() {
        EasyMock.expect(ClientBuilder.newClient()).andReturn(client);
        EasyMock.expect(client.target(EasyMock.anyObject(String.class))).andReturn(webTarget);
        EasyMock.expect(webTarget.path(EasyMock.anyObject(String.class))).andReturn(webTarget);
        EasyMock.expect(webTarget.request()).andReturn(builder);
        EasyMock.expect(builder.headers(headers)).andReturn(builder);
    }
}
