/*
 * Copyright 2017 ZTE Corporation.
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.easymock.EasyMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.holmes.common.dmaap.entity.PolicyMsg;
import org.onap.holmes.common.exception.CorrelationException;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest({Client.class, WebTarget.class, ClientBuilder.class, Response.class, Builder.class})
@RunWith(PowerMockRunner.class)
public class PublisherTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String URL = "http://localhost/dmaapTopic";

    @Test
    public void publish_exception() throws Exception {

        Publisher publisher = new Publisher();
        publisher.setUrl(URL);

        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to connect to DCAE");

        publisher.publish(new PolicyMsg());
    }

    @Test
    public void publish_normal() throws Exception {

        Publisher publisher = new Publisher();
        publisher.setUrl(URL);

        WebTarget target = PowerMock.createMock(WebTarget.class);
        Client client = PowerMock.createMock(Client.class);
        Builder builder = PowerMock.createMock(Builder.class);
        Response response = PowerMock.createMock(Response.class);
        PowerMock.mockStatic(ClientBuilder.class);

        EasyMock.expect(ClientBuilder.newClient()).andReturn(client);
        EasyMock.expect(client.target(publisher.getUrl())).andReturn(target);
        EasyMock.expect(target.request(MediaType.APPLICATION_JSON)).andReturn(builder);
        EasyMock.expect(builder.post(EasyMock.anyObject(Entity.class))).andReturn(response);
        EasyMock.expect(response.getStatus()).andReturn(HttpStatus.SC_OK);

        PowerMock.replayAll();

        assertThat(publisher.publish(new PolicyMsg()), is(true));

        PowerMock.verifyAll();
    }

}