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

package org.onap.holmes.common.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.msb.sdk.discovery.entity.MicroServiceInfo;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;

@PrepareForTest({MicroServiceConfig.class})
@RunWith(PowerMockRunner.class)
public class MsbRegisterTest {
    @Test
    public void test_register2Msb_normal() {
        MicroServiceInfo msi = new MicroServiceInfo();
        String[] msbAddrInfo = {"127.0.0.1", "80"};

        PowerMock.mockStatic(MicroServiceConfig.class);
        expect(MicroServiceConfig.getMsbIpAndPort()).andReturn(msbAddrInfo);

        JerseyClient mockedJerseyClient = createMock(JerseyClient.class);

        Client mockedClient = createMock(Client.class);
        expect(mockedJerseyClient.client(false)).andReturn(mockedClient);

        WebTarget mockedWebTarget = createMock(WebTarget.class);
        expect(mockedClient.target("http://127.0.0.1:80/api/microservices/v1/services")).andReturn(mockedWebTarget);


        expect(mockedWebTarget.queryParam("createOrUpdate", true)).andReturn(mockedWebTarget).times(2);

        Invocation.Builder mockedBuilder = createMock(Invocation.Builder.class);
        expect(mockedWebTarget.request(MediaType.APPLICATION_JSON)).andReturn(mockedBuilder).times(2);

        Response mockedResponse = createMock(Response.class);
        expect(mockedBuilder.post(Entity.entity(msi, MediaType.APPLICATION_JSON)))
                .andReturn(mockedResponse);
        expect(mockedResponse.getStatus()).andReturn(300);

        expect(mockedBuilder.post(Entity.entity(msi, MediaType.APPLICATION_JSON)))
                .andReturn(mockedResponse);
        expect(mockedResponse.getStatus()).andReturn(201);
        expect(mockedResponse.readEntity(String.class)).andReturn("Error");
        expect(mockedResponse.readEntity(String.class)).andReturn("{\"serviceName\":\"holmes-engine-mgmt\"," +
                "\"version\":\"v1\",\"url\":\"/api/holmes-engine-mgmt/v1\",\"protocol\":\"REST\"," +
                "\"visualRange\":\"0|1\",\"lb_policy\":\"\",\"publish_port\":\"\",\"namespace\":\"\"," +
                "\"network_plane_type\":\"\",\"host\":\"\",\"path\":\"/api/holmes-engine-mgmt/v1\"," +
                "\"enable_ssl\":true,\"nodes\":[{\"ip\":\"127.0.0.1\",\"port\":\"9102\",\"checkType\":\"\"," +
                "\"checkUrl\":\"\",\"tls_skip_verify\":true,\"ha_role\":\"\",\"nodeId\":\"_v1_holmes-engine-mgmt_127.0.0.1_9102\"," +
                "\"status\":\"passing\"}],\"metadata\":[],\"labels\":[],\"status\":\"1\",\"is_manual\":false}");


        MsbRegister msbRegister = new MsbRegister(mockedJerseyClient);

        PowerMock.replayAll();

        try {
            msbRegister.register2Msb(msi);
        } catch (CorrelationException e) {
            // Do nothing
        }

        PowerMock.verifyAll();
    }
}