/*-
 * ============LICENSE_START=======================================================
 * org.onap.holmes.common.aai
 * ================================================================================
 * Copyright (C) 2018-2021 Huawei, ZTE. All rights reserved.
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
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.utils.JerseyClient;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.ws.rs.core.Response;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.onap.holmes.common.config.MicroServiceConfig.MSB_ADDR;


@RunWith(PowerMockRunner.class)
@PrepareForTest({JerseyClient.class})
@SuppressStaticInitializationFor("org.onap.holmes.common.utils.JerseyClient")
public class AaiQuery4Ccvpn2Test {
    private static JsonObject data;

    private static AaiQuery4Ccvpn2 aai = AaiQuery4Ccvpn2.newInstance();

    private static Map<String, Object> headers = new HashMap<>();
    private static JerseyClient client;
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

        headers.put("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.put("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.put("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        Whitebox.setInternalState(aai, "headers", headers);
    }

    @Before
    public void before() throws Exception {
        client = PowerMock.createMock(JerseyClient.class);
        response = PowerMock.createMock(Response.class);
        PowerMock.expectNew(JerseyClient.class).andReturn(client).anyTimes();
    }

    @After
    public void after() {
        PowerMock.resetAll();
    }

    @Test
    public void test_getServiceInstances_exception() {
        mockGetMethod();
        EasyMock.expect(client.get(EasyMock.anyString())).andReturn(data.get("site-resources").toString());

        mockGetMethod();
        EasyMock.expect(client.get(EasyMock.anyString())).andReturn(data.get("499hkg9933NNN").toString());

        mockGetMethod();
        EasyMock.expect(client.get(EasyMock.anyString())).andReturn(data.get("499hkg9933NNN").toString());

        PowerMock.replayAll();

        aai.getSiteServiceInstance("HkHubONSDEMOBJHKCustomer");

        PowerMock.verifyAll();
    }

    @Test
    public void test_getServiceInstancesNull_exception() {
        mockGetMethod();
        EasyMock.expect(client.get(EasyMock.anyString())).andReturn(data.get("site-resources1").toString());

        PowerMock.replayAll();

        assertThat(aai.getSiteServiceInstance("HkHubONSDEMOSZHKCustomer"), is(nullValue()));

        PowerMock.verifyAll();
    }

    private void mockGetMethod() {
        EasyMock.expect(client.headers(headers)).andReturn(client);
        EasyMock.expect(client.path(EasyMock.anyString())).andReturn(client);
    }
}
