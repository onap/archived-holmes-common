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

import java.util.HashMap;
import java.util.Map;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.HttpsUtils;

public class AaiQuery {

    private AaiResponseUtil aaiResponseUtil;

    public VnfEntity getAaiVnfData(String vnfId, String vnfName) throws CorrelationException {
        String response = getVnfDataResponse(vnfId, vnfName);
        try {
            return aaiResponseUtil.convertJsonToVnfEntity(response);
        } catch (Exception e) {
            throw new CorrelationException("Failed to convert aai vnf response data to vnf entity", e);
        }
    }

    public VmEntity getAaiVmData(String vserverId, String vserverName) throws CorrelationException {
        String url = MicroServiceConfig.getMsbServerAddr() + getVmResourceLinks(vserverId, vserverName);
        String response = getResponse(url);
        try {
            return aaiResponseUtil.convertJsonToVmEntity(response);
        } catch (Exception e) {
            throw new CorrelationException("Failed to convert aai vm response data to vm entity", e);
        }
    }

    private String getVmResourceLinks(String vserverId, String vserverName) throws CorrelationException {
        String response = getResourceLinksResponse(vserverId, vserverName);
        try {
            return aaiResponseUtil.convertJsonToVmResourceLink(response).get(0).getResourceLink();
        } catch (Exception e) {
            throw new CorrelationException("Failed to get aai resource link", e);
        }
    }

    private String getResourceLinksResponse(String vserverId, String vserverName) throws CorrelationException {
        String url =
                MicroServiceConfig.getMsbServerAddr() + AaiConfig.VM_ADDR + "vserver-id:EQUALS:"
                        + vserverId;
        String response = getResponse(url);
        if (response.equals("")) {
            url = MicroServiceConfig.getMsbServerAddr() + AaiConfig.VM_ADDR
                    + "vserver-name:EQUALS:" + vserverName;
            response = getResponse(url);
        }
        return response;
    }

    private String getVnfDataResponse(String vnfId, String vnfName) throws CorrelationException {
        String url = MicroServiceConfig.getMsbServerAddr() + AaiConfig.VNF_ADDR + "vnf-id=" + vnfId;
        String response = getResponse(url);
        if (response.equals("")) {
            url = MicroServiceConfig.getMsbServerAddr() + AaiConfig.VNF_ADDR + "vnf-name="
                    + vnfName;
            response = getResponse(url);
        }
        return response;
    }

    private String getResponse(String url) throws CorrelationException {
        String response = "";
        try {
            response = HttpsUtils.get(url, getHeaders());
        } catch (Exception e) {
            throw new CorrelationException("Failed to get data from aai", e);
        }
        return response;
    }

    private Map getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.put("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.put("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.put("Accept", "application/json");
        return headers;
    }
}
