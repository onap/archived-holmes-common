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
package org.onap.holmes.common.dcae;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.ClientConfig;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.dcae.entity.DcaeConfigurations;
import org.onap.holmes.common.dcae.utils.DcaeConfigurationParser;
import org.onap.holmes.common.exception.CorrelationException;

public class DcaeConfigurationQuery {

    public static DcaeConfigurations getDcaeConfigurations(String hostname)
            throws CorrelationException {
        String serviceAddrInfo = MicroServiceConfig.getServiceAddrInfoFromCBS(hostname);
        String response;
        try {
            response = getDcaeResponse(serviceAddrInfo);
        } catch (Exception e) {
            throw new CorrelationException("Failed to connect to dcae", e);
        }
        DcaeConfigurations dcaeConfigurations = null;
        dcaeConfigurations = DcaeConfigurationParser.parse(response);
        return dcaeConfigurations;
    }

    private static String getDcaeResponse(String serviceAddrInfo) {
        Client client = ClientBuilder.newClient(new ClientConfig());
        WebTarget webTarget = client.target(serviceAddrInfo);
        return webTarget.request("application/json").get()
                .readEntity(String.class);
    }
}
