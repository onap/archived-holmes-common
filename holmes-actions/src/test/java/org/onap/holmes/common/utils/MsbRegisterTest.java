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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.msb.sdk.discovery.entity.MicroServiceFullInfo;
import org.onap.msb.sdk.discovery.entity.MicroServiceInfo;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import javax.ws.rs.client.Entity;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createPartialMock;

@PrepareForTest({MicroServiceConfig.class, JerseyClient.class})
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.net.ssl.*", "javax.security.*"})
public class MsbRegisterTest {

    private MsbRegister msbRegister;
    private JerseyClient mockedJerseyClient;
    private MicroServiceInfo msi;

    @Before
    public void before() {
        msi = new MicroServiceInfo();
        String[] msbAddrInfo = {"127.0.0.1", "80"};

        PowerMock.mockStatic(MicroServiceConfig.class);
        expect(MicroServiceConfig.getMsbIpAndPort()).andReturn(msbAddrInfo);

        mockedJerseyClient = createPartialMock(JerseyClient.class,
                "post", new Class[]{String.class, Entity.class, Class.class});

        msbRegister = new MsbRegister();
        WhiteboxImpl.setInternalState(msbRegister, "client", mockedJerseyClient);
    }

    @Test
    public void test_register2Msb_normal() {
        expect(mockedJerseyClient.post(anyObject(String.class),
                anyObject(Entity.class),
                anyObject(Class.class)))
                .andReturn(GsonUtil.jsonToBean("{\"serviceName\":\"holmes-engine-mgmt\"," +
                                "\"version\":\"v1\",\"url\":\"/api/holmes-engine-mgmt/v1\",\"protocol\":\"REST\"," +
                                "\"visualRange\":\"0|1\",\"lb_policy\":\"\",\"publish_port\":\"\",\"namespace\":\"\"," +
                                "\"network_plane_type\":\"\",\"host\":\"\",\"path\":\"/api/holmes-engine-mgmt/v1\"," +
                                "\"enable_ssl\":true,\"nodes\":[{\"ip\":\"127.0.0.1\",\"port\":\"9102\",\"checkType\":\"\"," +
                                "\"checkUrl\":\"\",\"tls_skip_verify\":true,\"ha_role\":\"\",\"nodeId\":\"_v1_holmes-engine-mgmt_127.0.0.1_9102\"," +
                                "\"status\":\"passing\"}],\"metadata\":[],\"labels\":[],\"status\":\"1\",\"is_manual\":false}",
                        MicroServiceFullInfo.class));

        PowerMock.replayAll();

        try {
            msbRegister.register2Msb(msi);
        } catch (CorrelationException e) {
            // Do nothing
        }

        PowerMock.verifyAll();
    }

    @Test
    public void test_register2Msb_fail_once() {
        expect(mockedJerseyClient.post(anyObject(String.class),
                anyObject(Entity.class),
                anyObject(Class.class)))
                .andReturn(null);

        expect(mockedJerseyClient.post(anyObject(String.class),
                anyObject(Entity.class),
                anyObject(Class.class)))
                .andReturn(GsonUtil.jsonToBean("{\"serviceName\":\"holmes-engine-mgmt\"," +
                                "\"version\":\"v1\",\"url\":\"/api/holmes-engine-mgmt/v1\",\"protocol\":\"REST\"," +
                                "\"visualRange\":\"0|1\",\"lb_policy\":\"\",\"publish_port\":\"\",\"namespace\":\"\"," +
                                "\"network_plane_type\":\"\",\"host\":\"\",\"path\":\"/api/holmes-engine-mgmt/v1\"," +
                                "\"enable_ssl\":true,\"nodes\":[{\"ip\":\"127.0.0.1\",\"port\":\"9102\",\"checkType\":\"\"," +
                                "\"checkUrl\":\"\",\"tls_skip_verify\":true,\"ha_role\":\"\",\"nodeId\":\"_v1_holmes-engine-mgmt_127.0.0.1_9102\"," +
                                "\"status\":\"passing\"}],\"metadata\":[],\"labels\":[],\"status\":\"1\",\"is_manual\":false}",
                        MicroServiceFullInfo.class));

        PowerMock.replayAll();

        try {
            msbRegister.register2Msb(msi);
        } catch (CorrelationException e) {
            // Do nothing
        }

        PowerMock.verifyAll();
    }
}