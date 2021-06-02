/**
 * Copyright 2021 ZTE Corporation.
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

package org.onap.holmes.common;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.MsbRegister;
import org.onap.msb.sdk.discovery.entity.MicroServiceInfo;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.slf4j.LoggerFactory;

@RunWith(PowerMockRunner.class)
public class AbstractServiceInitializerTest {

    @Test
    public void initialize_normal() throws Exception {

        AbstractServiceInitializer asi = PowerMock.createPartialMock(AbstractServiceInitializer.class,
                "createMicroServiceInfo");
        MsbRegister msbRegister = PowerMock.createMock(MsbRegister.class);
        WhiteboxImpl.setInternalState(asi, "msbRegister", msbRegister);

        PowerMock.expectPrivate(asi, "createMicroServiceInfo").andReturn(new MicroServiceInfo());
        msbRegister.register2Msb(EasyMock.anyObject(MicroServiceInfo.class));
        EasyMock.expectLastCall();

        PowerMock.replayAll();

        WhiteboxImpl.invokeMethod(asi, "initialize");

        PowerMock.verifyAll();
    }

    @Test
    public void initialize_throw_exception() throws Exception {

        AbstractServiceInitializer asi = PowerMock.createPartialMock(AbstractServiceInitializer.class,
                "createMicroServiceInfo");
        MsbRegister msbRegister = PowerMock.createMock(MsbRegister.class);
        WhiteboxImpl.setInternalState(asi, "msbRegister", msbRegister);
        WhiteboxImpl.setInternalState(asi, "logger", LoggerFactory.getLogger(AbstractServiceInitializer.class));

        PowerMock.expectPrivate(asi, "createMicroServiceInfo").andReturn(new MicroServiceInfo());
        msbRegister.register2Msb(EasyMock.anyObject(MicroServiceInfo.class));
        EasyMock.expectLastCall().andThrow(new CorrelationException("any reason"));

        PowerMock.replayAll();

        WhiteboxImpl.invokeMethod(asi, "initialize");

        PowerMock.verifyAll();
    }
}