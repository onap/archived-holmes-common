/**
 * Copyright 2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.onap.holmes.common.aai;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import org.glassfish.jersey.client.ClientConfig;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;

public class AaiQuery {

    private AaiResponseUtil aaiResponseUtil;

    public VnfEntity getAaiVnfData(String vnfId, String vnfName) throws CorrelationException {
        Client client = ClientBuilder.newClient(new ClientConfig());
        WebTarget webTarget = client
                .target(MicroServiceConfig.getMsbServerAddr() + AaiConfig.VNF_ADDR + "vnf-id="
                        + vnfId);
        String response = webTarget.request("application/json").headers(getHeaders()).get()
                .readEntity(String.class);
        if (response == null) {
            webTarget = client
                    .target(MicroServiceConfig.getMsbServerAddr() + AaiConfig.VNF_ADDR + "vnf-name="
                            + vnfName);
            response = webTarget.request("application/json").headers(getHeaders()).get()
                    .readEntity(String.class);
        }
        try {
            return aaiResponseUtil.convertJsonToVnfEntity(response);
        } catch (Exception e) {
            throw new CorrelationException("Failed to convert aai vnf response data to vnf entity", e);
        }
    }

    public VmEntity getAaiVmData(String vserverId, String vserverName) throws CorrelationException {
        Client client = ClientBuilder.newClient(new ClientConfig());
        String response = client
                .target(MicroServiceConfig.getMsbServerAddr() + getVmResourceLinks(client,
                        vserverId, vserverName)).request("application/json").headers(getHeaders())
                .get().readEntity(String.class);
        try {
            return aaiResponseUtil.convertJsonToVmEntity(response);
        } catch (Exception e) {
            throw new CorrelationException("Failed to convert aai vm response data to vm entity", e);
        }
    }

    private String getVmResourceLinks(Client client, String vserverId, String vserverName) throws CorrelationException {
        WebTarget webTarget = client
                .target(MicroServiceConfig.getMsbServerAddr() + AaiConfig.VM_ADDR
                        + "vserver-id:EQUALS:" + vserverId);
        String response = webTarget.request("application/json").headers(getHeaders()).get()
                .readEntity(String.class);
        if (response == null) {
            webTarget = client.target(MicroServiceConfig.getMsbServerAddr() + AaiConfig.VM_ADDR
                    + "vserver-name:EQUALS:" + vserverName);
            response = webTarget.request("application/json").headers(getHeaders()).get()
                    .readEntity(String.class);
        }
        try {
            return aaiResponseUtil.convertJsonToVmResourceLink(response).get(0).getResourceLink();
        } catch (Exception e) {
            throw new CorrelationException("Failed to get aai resource link", e);
        }
    }

    private MultivaluedHashMap getHeaders() {
        MultivaluedHashMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.add("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.add("Authorization", AaiConfig.getAuthenticationCredentials());
        return headers;
    }
}
