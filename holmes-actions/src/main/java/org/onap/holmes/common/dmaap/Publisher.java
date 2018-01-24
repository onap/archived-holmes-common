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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpStatus;
import org.glassfish.jersey.client.ClientConfig;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.dmaap.entity.PolicyMsg;
import org.onap.holmes.common.exception.CorrelationException;

@Getter
@Setter
@Service
public class Publisher {

    private String topic;
    private String url;
    private String authInfo;
    private String authExpDate;

    public boolean publish(PolicyMsg msg) throws CorrelationException {
        Client client = ClientBuilder.newClient(new ClientConfig());
        ObjectMapper mapper = new ObjectMapper();
        String content = null;
        try {
            content = mapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            throw new CorrelationException("Failed to convert the message object to a json string.",
                    e);
        }
        WebTarget webTarget = client.target(url);
        Response response = null;
        try {
            response = webTarget.request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(content, MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            throw new CorrelationException("Failed to connect to DCAE.", e);
        }
        return checkStatus(response);
    }

    private boolean checkStatus(Response response) {
        return response.getStatus() == HttpStatus.SC_OK;
    }
}
