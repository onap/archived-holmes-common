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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.easymock.EasyMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.holmes.common.dmaap.entity.PolicyMsg;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.HttpsUtils;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@PrepareForTest({HttpsUtils.class, HttpResponse.class, Publisher.class})
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

        PowerMock.mockStatic(HttpsUtils.class);
        CloseableHttpClient httpClient = PowerMock.createMock(CloseableHttpClient.class);
        EasyMock.expect(HttpsUtils.getConditionalHttpsClient(HttpsUtils.DEFUALT_TIMEOUT)).andReturn(httpClient);
        HttpResponse httpResponse = PowerMock.createMock(HttpResponse.class);
        EasyMock.expect(HttpsUtils
                .post(EasyMock.anyObject(HttpPost.class), EasyMock.anyObject(HashMap.class),
                        EasyMock.anyObject(HashMap.class), EasyMock.anyObject(StringEntity.class),
                        EasyMock.anyObject(CloseableHttpClient.class))).andReturn(httpResponse);
        StatusLine statusLine = PowerMock.createMock(StatusLine.class);
        EasyMock.expect(httpResponse.getStatusLine()).andReturn(statusLine);
        EasyMock.expect(statusLine.getStatusCode()).andReturn(HttpStatus.SC_OK);
        httpClient.close();
        EasyMock.expectLastCall();

        PowerMock.replayAll();

        assertThat(publisher.publish(new PolicyMsg()), is(true));

        PowerMock.verifyAll();
    }

}