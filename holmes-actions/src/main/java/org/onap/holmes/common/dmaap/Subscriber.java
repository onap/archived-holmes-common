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

import java.util.Collections;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;
import org.glassfish.jersey.client.ClientConfig;
import org.onap.holmes.common.dmaap.entity.VesAlarm;
import org.onap.holmes.common.exception.CorrelationException;

@Getter
@Setter
public class Subscriber {
    /**
     * The number of milliseconds to wait for messages if none are immediately
     * available. This should normally be used, and set at 15000 or higher.
     */
    private int timeout = 15000;

    /**
     * The maximum number of messages to return
     */
    private int limit = 100;

    private boolean secure;
    private String topic;
    private String url;
    private String consumerGroup = "g0";
    private String consumer = "u1";
    private String authInfo;
    private String authExpDate;

    List<VesAlarm> subscribe() throws CorrelationException {
        Client client = ClientBuilder.newClient(new ClientConfig());
        WebTarget webTarget = client.target(url);
        Response response = webTarget.path(topic).path(consumerGroup).path(consumer).request().get();

        try {
            return extractAlarms(response);
        }
        catch (Exception e) {
            throw new CorrelationException("Failed to convert the response data to VES alarms.", e);
        }
    }

    private List<VesAlarm> extractAlarms(Response response) {
        return Collections.emptyList();
    }
}
