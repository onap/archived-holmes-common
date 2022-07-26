/*
 * Copyright 2017-2020 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.holmes.common.dmaap;

import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.holmes.common.dmaap.entity.PolicyMsg;
import org.onap.holmes.common.utils.JerseyClient;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import jakarta.ws.rs.client.Entity;

import static org.easymock.EasyMock.anyObject;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.net.ssl.*", "javax.security.*"})
public class PublisherTest {

    @Test
    public void publish_normal() {

        Publisher publisher = new Publisher();
        publisher.setUrl("http://localhost/dmaapTopic");

        JerseyClient mockedJerseyClient = PowerMock.createMock(JerseyClient.class);
        WhiteboxImpl.setInternalState(publisher, "client", mockedJerseyClient);
        EasyMock.expect(mockedJerseyClient.post(anyObject(String.class), anyObject(Entity.class)))
                .andReturn(StringUtils.EMPTY);

        PowerMock.replayAll();

        publisher.publish(new PolicyMsg());

        PowerMock.verifyAll();
    }

}