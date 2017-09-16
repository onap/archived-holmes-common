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

import static org.onap.holmes.common.config.MicroServiceConfig.HOSTNAME;

import com.eclipsesource.jaxrs.consumer.ConsumerFactory;
import javax.ws.rs.QueryParam;
import org.easymock.EasyMock;
import org.junit.Rule;
import org.junit.Test;
import org.onap.holmes.common.msb.MicroserviceBusRest;
import org.onap.holmes.common.api.entity.ServiceRegisterEntity;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

@PrepareForTest({ConsumerFactory.class, MicroServiceConfig.class})
@PowerMockIgnore({"javax.ws.*"})
public class MSBRegisterUtilTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();
    private MSBRegisterUtil msbRegisterUtil = new MSBRegisterUtil();
    private MicroserviceBusRest microserviceBusRest = new MicroserviceBusRestProxy();

    @Test
    public void registerTest() throws Exception {
        System.setProperty(HOSTNAME, "10.74.5.8:1545");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{}");

        PowerMock.mockStatic(ConsumerFactory.class);
        EasyMock.expect(ConsumerFactory
                .createConsumer(EasyMock.anyObject(String.class), EasyMock.anyObject(Class.class)))
                .andReturn(microserviceBusRest);
        PowerMock.replayAll();

        msbRegisterUtil.register(initServiceEntity());

        PowerMock.verifyAll();

        System.clearProperty(HOSTNAME);
    }

    private ServiceRegisterEntity initServiceEntity() {
        String[] serviceAddrInfo = MicroServiceConfig.getServiceAddrInfo();
        ServiceRegisterEntity serviceRegisterEntity = new ServiceRegisterEntity();
        serviceRegisterEntity.setServiceName("holmes-rule-mgmt");
        serviceRegisterEntity.setProtocol("REST");
        serviceRegisterEntity.setVersion("v1");
        serviceRegisterEntity.setUrl("/api/holmes-rule-mgmt/v1");
        serviceRegisterEntity.setSingleNode(serviceAddrInfo[0], serviceAddrInfo[1], 0);
        serviceRegisterEntity.setVisualRange("1|0");
        return serviceRegisterEntity;
    }

    class MicroserviceBusRestProxy implements MicroserviceBusRest {

        @Override
        public ServiceRegisterEntity registerServce(@QueryParam("createOrUpdate") String createOrUpdate,
                ServiceRegisterEntity entity) {
            return null;
        }
    }
}