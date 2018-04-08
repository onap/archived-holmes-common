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

import java.util.HashMap;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.easymock.EasyMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.onap.holmes.common.dmaap.entity.PolicyMsg;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.HttpsUtils;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest({HttpsUtils.class, HttpResponse.class})
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

        PowerMockito.mockStatic(HttpsUtils.class);
        HttpResponse httpResponse = PowerMockito.mock(HttpResponse.class);
        PowerMockito.when(HttpsUtils
                .post(Matchers.eq("http://localhost/dmaapTopic"), Matchers.any(HashMap.class),
                        Matchers.any(HashMap.class), Matchers.any(StringEntity.class),
                        Matchers.any(CloseableHttpClient.class))).thenReturn(httpResponse);
        StatusLine statusLine = PowerMockito.mock(StatusLine.class);
        PowerMockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
        PowerMockito.when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        PowerMock.replayAll();

        assertThat(publisher.publish(new PolicyMsg()), is(true));

        PowerMock.verifyAll();
    }

}