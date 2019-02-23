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

import org.easymock.EasyMock;
import org.junit.Rule;
import org.junit.Test;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.msb.sdk.discovery.entity.MicroServiceFullInfo;
import org.onap.msb.sdk.discovery.entity.MicroServiceInfo;
import org.onap.msb.sdk.httpclient.msb.MSBServiceClient;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

@PrepareForTest({MicroServiceConfig.class, MSBServiceClient.class, MSBRegisterUtil.class})
@PowerMockIgnore({"javax.ws.*"})
public class MSBRegisterUtilTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private MSBRegisterUtil msbRegisterUtil = new MSBRegisterUtil();

    @Test
    public void test_register2Msb_normal() throws Exception {
        MicroServiceInfo msi = new MicroServiceInfo();
        String[] msbAddrInfo = {"127.0.0.1", "80"};

        PowerMock.mockStatic(MicroServiceConfig.class);
        EasyMock.expect(MicroServiceConfig.getMsbIpAndPort()).andReturn(msbAddrInfo);

        MSBServiceClient client = PowerMock.createMock(MSBServiceClient.class);
        PowerMock.expectNew(MSBServiceClient.class, msbAddrInfo[0], Integer.parseInt(msbAddrInfo[1])).andReturn(client);

        EasyMock.expect(client.registerMicroServiceInfo(msi, false)).andReturn(null);

        EasyMock.expect(client.registerMicroServiceInfo(msi, false)).andReturn(new MicroServiceFullInfo());

        PowerMock.replayAll();

        try {
            msbRegisterUtil.register2Msb(msi);
        } catch (CorrelationException e) {
            // Do nothing
        }

        PowerMock.verifyAll();
    }
}